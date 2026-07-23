package x10.trainup.order.core.usecases;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.entities.OrderEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.order.core.repositories.IOrderRepository;
import x10.trainup.order.core.usecases.createOrder.CreateOrderReq;
import x10.trainup.order.core.usecases.createOrder.ICreateOrderUc;
import x10.trainup.order.core.errors.OrderError;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import x10.trainup.product.core.repositories.IRepositoryProduct;
import x10.trainup.commons.domain.entities.ProductEntity;
import x10.trainup.commons.domain.entities.OrderItemEntity;
import x10.trainup.commons.domain.entities.SizeVariantEntity;
import x10.trainup.commons.domain.entities.FlavorVariantEntity;
import x10.trainup.commons.domain.enums.OrderStatus;
import x10.trainup.loyalty.core.usecases.ILoyaltyService;
import x10.trainup.user.core.usecases.ICoreUserSerivce;
import x10.trainup.commons.domain.entities.UserEntity;

@AllArgsConstructor
@Service
public class CoreServiceOrderImpl implements ICoreOrderService {

    private final ICreateOrderUc iCreateOrderUc;
    private final IOrderRepository iOrderRepository;
    private final IRepositoryProduct productRepository;
    private final ILoyaltyService iLoyaltyService;
    private final ICoreUserSerivce iCoreUserSerivce;

    @Override
    public OrderEntity createOrder(CreateOrderReq req) {
        return iCreateOrderUc.process(req);
    }

    @Override
    public List<OrderEntity> getAllOrdersSortedByDateDesc() {
        return iOrderRepository.findAllOrderByCreatedAtDesc();
    }

    @Override
    public List<OrderEntity> getOrdersByUserId(String userId) {
        return iOrderRepository.findByUserId(userId);
    }

    @Override
    public List<OrderEntity> getOrdersByGuestEmail(String email) {
        List<OrderEntity> ordersByEmail = iOrderRepository.findByShippingEmail(email);

        Optional<UserEntity> userOpt = iCoreUserSerivce.getUserByEmail(email);
        if (userOpt.isPresent()) {
            List<OrderEntity> ordersByUserId = iOrderRepository.findByUserId(userOpt.get().getId());
            
            java.util.Map<String, OrderEntity> uniqueOrders = new java.util.LinkedHashMap<>();
            ordersByEmail.forEach(o -> uniqueOrders.put(o.getId(), o));
            ordersByUserId.forEach(o -> uniqueOrders.put(o.getId(), o));
            
            return uniqueOrders.values().stream()
                    .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                    .collect(java.util.stream.Collectors.toList());
        }

        return ordersByEmail;
    }

    @Override
    public OrderEntity updateOrderStatus(UpdateOrderStatusReq req) {

        OrderEntity order = iOrderRepository.findById(req.getOrderId())
                .orElseThrow(() ->
                        new BusinessException(
                                OrderError.ORDER_NOT_FOUND,
                                "Order not found: " + req.getOrderId()));

        // Chỉ trừ kho khi chuyển từ trạng thái khác sang CONFIRMED
        if (order.getStatus() != OrderStatus.CONFIRMED
                && req.getStatus() == OrderStatus.CONFIRMED) {

            for (OrderItemEntity item : order.getItems()) {

                ProductEntity product = productRepository.findById(item.getProductId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found: " + item.getProductId()));

                SizeVariantEntity size = product.getSizes()
                        .stream()
                        .filter(s -> s.getId().equals(item.getSizeId()))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Size not found: " + item.getSizeId()));

                FlavorVariantEntity flavor = size.getFlavors()
                        .stream()
                        .filter(f -> f.getId().equals(item.getFlavorId()))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Flavor not found: " + item.getFlavorId()));

                // Kiểm tra tồn kho
                if (flavor.getQuantityInStock() < item.getQuantity()) {
                    throw new RuntimeException(
                            "Không đủ tồn kho cho sản phẩm: "
                                    + item.getProductName()
                                    + " - "
                                    + flavor.getFlavor());
                }

                // Trừ tồn kho
                flavor.setQuantityInStock(
                        flavor.getQuantityInStock() - item.getQuantity());

                // Tăng số lượng đã bán
                flavor.setQuantitySold(
                        flavor.getQuantitySold() + item.getQuantity());

                // Lưu Product
                System.out.println("========== BEFORE SAVE ==========");
                System.out.println("Product: " + product.getName());
                System.out.println("Flavor: " + flavor.getFlavor());
                System.out.println("Stock: " + flavor.getQuantityInStock());
                System.out.println("Sold: " + flavor.getQuantitySold());

                productRepository.save(product);

                System.out.println("========== AFTER SAVE ==========");
            }
        }
        
        // Thêm điểm và hạng Loyalty khi CONFIRMED (Đã xác nhận)
        if (order.getStatus() != OrderStatus.CONFIRMED
                && req.getStatus() == OrderStatus.CONFIRMED) {
            if (order.getUserId() != null) {
                try {
                    iLoyaltyService.addPointsAndCheckTier(order.getUserId(), order.getTotalAmount());
                } catch (Exception e) {
                    System.err.println("Error adding loyalty points: " + e.getMessage());
                }
            }
        }

        order.setStatus(req.getStatus());
        order.setUpdatedAt(Instant.now());

        return iOrderRepository.save(order);
    }

    @Override
    public java.util.Optional<OrderEntity> getOrderById(String orderId) {
        return iOrderRepository.findById(orderId);
    }

}
