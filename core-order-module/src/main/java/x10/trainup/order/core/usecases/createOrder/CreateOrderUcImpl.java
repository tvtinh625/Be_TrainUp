package x10.trainup.order.core.usecases.createOrder;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.address.core.dto.ShippingFeeRequest;
import x10.trainup.address.core.usecases.IAddressService;
import x10.trainup.commons.domain.entities.*;
import x10.trainup.commons.domain.enums.OrderStatus;
import x10.trainup.commons.domain.enums.PaymentStatus;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.order.core.errors.OrderError;
import x10.trainup.order.core.repositories.IOrderRepository;
import x10.trainup.product.core.repositories.IRepositoryProduct;
import x10.trainup.user.core.usecases.ICoreUserSerivce;
import x10.trainup.user.core.usecases.createGuestUserUc.CreateGuestUserReq;

import x10.trainup.coupon.core.usecases.ICouponService;

import x10.trainup.loyalty.core.usecases.ILoyaltyService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@AllArgsConstructor
public class CreateOrderUcImpl implements ICreateOrderUc {

    private final IOrderRepository iOrderRepository;
    private final ICoreUserSerivce iCoreUserSerivce;
    private final IRepositoryProduct iRepositoryProduct;
    private final IAddressService iAddressService;
    private final ICouponService iCouponService;
    private final ILoyaltyService iLoyaltyService;

    // Cấu hình mặc định cho shop/warehouse
    private static final int DEFAULT_FROM_DISTRICT_ID = 1455; // Quận 1, TP.HCM (example)
    private static final String DEFAULT_FROM_WARD_CODE = "21408"; // Phường Bến Nghé (example)
    private static final int DEFAULT_SERVICE_TYPE_ID = 2; // 2 = Express, 5 = Tiết kiệm

    @Override
    public OrderEntity process(CreateOrderReq req) {
        UserEntity user = getOrCreateGuestUser(req);
        List<OrderItemEntity> orderItems = buildOrderItems(req.getItems());
        BigDecimal subtotal = calculateSubtotal(orderItems);
        BigDecimal shippingFee = calculateShippingFeeFromAddress(
                req.getShippingAddress(),
                subtotal,
                orderItems
        );
        
        // Tính giảm giá từ Coupon
        BigDecimal couponDiscount = BigDecimal.ZERO;
        String couponCode = req.getCouponCode();
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            couponDiscount = iCouponService.calculateDiscount(couponCode, subtotal);
        }

        // Tính giảm giá từ Loyalty Tier (nếu user không phải guest)
        BigDecimal tierDiscount = BigDecimal.ZERO;
        if (!x10.trainup.commons.domain.enums.AuthProvider.GUEST.equals(user.getProvider())) {
            BigDecimal tierDiscountPercent = iLoyaltyService.getTierDiscountPercentage(user.getTier());
            if (tierDiscountPercent.compareTo(BigDecimal.ZERO) > 0) {
                tierDiscount = subtotal.multiply(tierDiscountPercent).divide(BigDecimal.valueOf(100));
            }
        }

        // Tính giảm giá từ điểm Loyalty
        BigDecimal pointsDiscount = BigDecimal.ZERO;
        Long pointsRedeemed = req.getPointsToRedeem();
        if (pointsRedeemed != null && pointsRedeemed > 0) {
            // 1 điểm = 1 VNĐ
            pointsDiscount = BigDecimal.valueOf(pointsRedeemed);
            
            // Validate user có đủ điểm
            if (user.getLoyaltyPoints() == null || user.getLoyaltyPoints() < pointsRedeemed) {
                throw new BusinessException(OrderError.INVALID_ORDER_ITEM, "Không đủ điểm tích lũy");
            }
        }

        BigDecimal totalAmount = subtotal.add(shippingFee)
                .subtract(couponDiscount)
                .subtract(tierDiscount)
                .subtract(pointsDiscount);
                
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        ShippingAddressEntity shippingAddress = buildShippingAddress(req.getShippingAddress());

        // 7. Tạo order entity
        OrderEntity order = OrderEntity.builder()
                .id(UUID.randomUUID().toString())
                .orderNumber(generateOrderNumber())
                .userId(user.getId())
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .paymentMethod(req.getPaymentMethod())
                .items(orderItems)
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .couponCode(couponCode)
                .couponDiscount(couponDiscount)
                .tierDiscount(tierDiscount)
                .pointsRedeemed(pointsRedeemed)
                .pointsDiscount(pointsDiscount)
                .totalAmount(totalAmount)
                .shippingAddress(shippingAddress)
                .note(req.getNote())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        OrderEntity savedOrder = iOrderRepository.save(order);
        
        // Đánh dấu mã đã dùng
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            iCouponService.markCouponUsed(couponCode);
        }
        
        // Trừ điểm Loyalty
        if (pointsRedeemed != null && pointsRedeemed > 0) {
            iLoyaltyService.redeemPoints(user.getId(), pointsRedeemed);
        }
        
        return savedOrder;
    }


    private BigDecimal calculateShippingFeeFromAddress(
            ShippingAddressReq shippingAddress,
            BigDecimal subtotal,
            List<OrderItemEntity> orderItems
    ) {
        try {
            // Tính tổng khối lượng (nếu có weight trong product)
            // Mặc định 500g nếu không có thông tin
            int totalWeight = calculateTotalWeight(orderItems);

            // Build request để tính phí ship
            ShippingFeeRequest feeRequest = new ShippingFeeRequest();
            feeRequest.setFromDistrictId(DEFAULT_FROM_DISTRICT_ID);
            feeRequest.setFromWardCode(DEFAULT_FROM_WARD_CODE);
            feeRequest.setToDistrictId(shippingAddress.getDistrictId());
            feeRequest.setToWardCode(String.valueOf(shippingAddress.getWardCode()));
            feeRequest.setWeight(totalWeight);
            feeRequest.setInsuranceValue(subtotal.intValue());
            feeRequest.setServiceTypeId(DEFAULT_SERVICE_TYPE_ID);

            double fee = iAddressService.calculateShippingFee(feeRequest);

            return BigDecimal.valueOf(fee);

        } catch (Exception e) {
            // Nếu lỗi khi tính phí ship, dùng phí mặc định
            throw new BusinessException(
                    OrderError.SHIPPING_FEE_CALCULATION_FAILED,
                    "Failed to calculate shipping fee: " + e.getMessage(),
                    Map.of(
                            "districtId", shippingAddress.getDistrictId(),
                            "wardCode", shippingAddress.getWardCode()
                    )
            );
        }
    }

    /**
     * Tính tổng khối lượng đơn hàng
     * Nếu product có thông tin weight thì dùng, không thì dùng mặc định
     */
    private int calculateTotalWeight(List<OrderItemEntity> orderItems) {
        // Mặc định mỗi item = 500g
        int defaultWeightPerItem = 500;
        int totalWeight = 0;

        for (OrderItemEntity item : orderItems) {
            // TODO: Nếu ProductEntity có field weight thì lấy từ đó
            // int itemWeight = product.getWeight() != null ? product.getWeight() : defaultWeightPerItem;
            int itemWeight = defaultWeightPerItem;
            totalWeight += itemWeight * item.getQuantity();
        }

        return totalWeight;
    }

    private UserEntity getOrCreateGuestUser(CreateOrderReq req) {
        if (req.getUserId() != null) {
            Optional<UserEntity> existingUser = iCoreUserSerivce.getUserById(req.getUserId());
            if (existingUser.isPresent()) {
                return existingUser.get();
            }
        }

        ShippingAddressReq ship = req.getShippingAddress();
        String email = ship.getEmail();

        if (email != null && !email.trim().isEmpty()) {
            Optional<UserEntity> userByEmail = iCoreUserSerivce.getUserByEmail(email.trim());
            if (userByEmail.isPresent()) {
                return userByEmail.get();
            }
        }

        CreateGuestUserReq guestReq = CreateGuestUserReq.builder()
                .recipientName(ship.getRecipientName())
                .phone(ship.getPhoneNumber())
                .email(email)
                .street(ship.getAddress())
                .ward(ship.getWard())
                .district(ship.getDistrict())
                .province(ship.getCity())
                .country("Vietnam")
                .wardCode(ship.getWardCode())
                .provinceId(ship.getProvinceId())
                .districtId(ship.getDistrictId())
                .build();

        UserEntity guest = iCoreUserSerivce.createGuestUser(guestReq);
        return guest;
    }

    private List<OrderItemEntity> buildOrderItems(List<OrderItemReq> itemsReq) {
        List<OrderItemEntity> orderItems = new ArrayList<>();
        for (OrderItemReq itemReq : itemsReq) {
            // 1. Validate product tồn tại
            ProductEntity product = iRepositoryProduct.findById(itemReq.getProductId())
                    .orElseThrow(() -> new BusinessException(
                            OrderError.INVALID_ORDER_ITEM,
                            "Product not found: " + itemReq.getProductId(),
                            Map.of("productId", itemReq.getProductId())
                    ));

            // 2. Validate size tồn tại
            if (!iRepositoryProduct.existsSize(itemReq.getProductId(), itemReq.getSizeId())) {
                throw new BusinessException(
                        OrderError.INVALID_ORDER_ITEM,
                        "Size not found for product: " + itemReq.getProductId(),
                        Map.of(
                                "productId", itemReq.getProductId(),
                                "sizeId", itemReq.getSizeId()
                        )
                );
            }

            // 3. Validate flavor tồn tại
            if (!iRepositoryProduct.existsFlavor(itemReq.getProductId(), itemReq.getSizeId(), itemReq.getFlavorId())) {
                throw new BusinessException(
                        OrderError.INVALID_ORDER_ITEM,
                        "Flavor not found for product: " + itemReq.getProductId(),
                        Map.of(
                                "productId", itemReq.getProductId(),
                                "sizeId", itemReq.getSizeId(),
                                "flavorId", itemReq.getFlavorId()
                        )
                );
            }

            // 4. Lấy SizeVariantEntity
            SizeVariantEntity size = product.getSizes()
                    .stream()
                    .filter(s -> s.getId().equals(itemReq.getSizeId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(
                            OrderError.INVALID_ORDER_ITEM,
                            "Size entity not found in product sizes",
                            Map.of(
                                    "productId", itemReq.getProductId(),
                                    "sizeId", itemReq.getSizeId()
                            )
                    ));

            // 5. Lấy FlavorVariantEntity và tên flavor
            FlavorVariantEntity flavor = size.getFlavors()
                    .stream()
                    .filter(f -> f.getId().equals(itemReq.getFlavorId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(
                            OrderError.INVALID_ORDER_ITEM,
                            "Flavor entity not found in size flavors",
                            Map.of(
                                    "productId", itemReq.getProductId(),
                                    "sizeId", itemReq.getSizeId(),
                                    "flavorId", itemReq.getFlavorId()
                            )
                    ));

            // 6. Lấy giá theo size (ưu tiên discountPrice)
            BigDecimal unitPrice = (size.getDiscountPrice() != null
                    && size.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0)
                    ? size.getDiscountPrice()
                    : size.getPrice();

            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            // 7. Build OrderItemEntity với đầy đủ thông tin
            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .productId(itemReq.getProductId())
                    .productName(product.getName())
                    .sizeId(itemReq.getSizeId())
                    .sizeName(size.getSize())
                    .flavorId(itemReq.getFlavorId())
                    .flavorName(flavor.getFlavor()) // ✅ Lấy tên flavor từ FlavorVariantEntity
                    .price(unitPrice)
                    .quantity(itemReq.getQuantity())
                    .subtotal(subtotal)
                    .imageUrl(size.getImageUrl())
                    .build();

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private BigDecimal calculateSubtotal(List<OrderItemEntity> items) {
        return items.stream()
                .map(OrderItemEntity::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private ShippingAddressEntity buildShippingAddress(ShippingAddressReq req) {
        return ShippingAddressEntity.builder()
                .recipientName(req.getRecipientName())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .address(req.getAddress())
                .ward(req.getWard())
                .district(req.getDistrict())
                .city(req.getCity())
                .districtId(req.getDistrictId())
                .provinceId(req.getProvinceId())
                .wardCode(req.getWardCode())
                .country("Việt Nam")
                .build();
    }

    private String generateOrderNumber() {
        String dateStr = Instant.now().toString().substring(0, 10).replace("-", "");
        String randomSuffix = String.format("%03d", (int) (Math.random() * 1000));
        return "ORD-" + dateStr + "-" + randomSuffix;
    }
}