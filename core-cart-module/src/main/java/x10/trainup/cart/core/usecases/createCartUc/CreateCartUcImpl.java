package x10.trainup.cart.core.usecases.createCartUc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.cart.core.error.CartError;
import x10.trainup.cart.core.repository.IRepositoryCart;
import x10.trainup.commons.domain.entities.CartEntity;
import x10.trainup.commons.domain.entities.CartItemEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.product.core.repositories.IRepositoryProduct;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.usecases.ICoreUserSerivce;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CreateCartUcImpl implements ICreateCartUc {

    private final ICoreUserSerivce iCoreUserSerivce;
    private final IRepositoryCart iRepositoryCart;
    private final IRepositoryProduct iRepositoryProduct;

    @Override
    public CreateCartRes addToCart(CreateCartReq req, String userId) {

        // 1️⃣ Check user tồn tại
        if (!iCoreUserSerivce.existsById(userId)) {
            throw new BusinessException(UserError.USER_NOT_FOUND, "Không tìm thấy người dùng.");
        }

        // 2️⃣ Lấy cart hiện tại của user, nếu chưa có tạo mới
        Optional<CartEntity> optionalCart = iRepositoryCart.findByUserId(userId);
        CartEntity cart = optionalCart.orElseGet(() -> CartEntity.builder()
                .userId(userId)
                .items(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build()
        );

        // 3️⃣ Thêm hoặc cộng item
        for (CartItemReq itemReq : req.getItems()) {

            // 🔹 Kiểm tra product tồn tại
            var productOpt = iRepositoryProduct.findById(itemReq.getProductId());
            if (!productOpt.isPresent()) {
                throw new BusinessException(CartError.PRODUCT_NOT_FOUND,
                        "ProductId: " + itemReq.getProductId() + " không tồn tại.");
            }
            var product = productOpt.get();

            // 🔹 Kiểm tra sizeId tồn tại
            if (itemReq.getSizeId() != null &&
                    !iRepositoryProduct.existsSize(itemReq.getProductId(), itemReq.getSizeId())) {
                throw new BusinessException(CartError.SIZE_NOT_FOUND,
                        "SizeId: " + itemReq.getSizeId() + " không tồn tại.");
            }

            // 🔹 Kiểm tra flavorId tồn tại trong size
            if (itemReq.getFlavorId() != null &&
                    !iRepositoryProduct.existsFlavor(itemReq.getProductId(), itemReq.getSizeId(), itemReq.getFlavorId())) {
                throw new BusinessException(CartError.FLAVOR_NOT_FOUND,
                        "FlavorId: " + itemReq.getFlavorId() + " không tồn tại.");
            }

            // 🔹 Kiểm tra số lượng tồn kho
            if (itemReq.getFlavorId() != null && itemReq.getSizeId() != null) {
                // Tính tổng quantity (quantity đang có trong cart + quantity mới thêm)
                int totalRequestedQuantity = itemReq.getQuantity();

                for (CartItemEntity existingItem : cart.getItems()) {
                    if (existingItem.getProductId().equals(itemReq.getProductId())
                            && existingItem.getSizeId() != null && existingItem.getSizeId().equals(itemReq.getSizeId())
                            && existingItem.getFlavorId() != null && existingItem.getFlavorId().equals(itemReq.getFlavorId())
                    ) {
                        totalRequestedQuantity += existingItem.getQuantity();
                        break;
                    }
                }

                // Lấy thông tin flavor từ product
                var sizeOpt = product.getSizes().stream()
                        .filter(s -> s.getId().equals(itemReq.getSizeId()))
                        .findFirst();

                if (sizeOpt.isPresent()) {
                    var flavorOpt = sizeOpt.get().getFlavors().stream()
                            .filter(f -> f.getId().equals(itemReq.getFlavorId()))
                            .findFirst();

                    if (flavorOpt.isPresent()) {
                        var flavor = flavorOpt.get();

                        // Kiểm tra flavor có đang active không
                        if (!flavor.isActive()) {
                            throw new BusinessException(CartError.PRODUCT_INACTIVE,
                                    "Sản phẩm '" + product.getName() + "' với hương vị '" +
                                            itemReq.getFlavorName() + "' đã ngưng kinh doanh.");
                        }

                        // Kiểm tra số lượng tồn kho
                        int quantityInStock = flavor.getQuantityInStock();
                        System.out.println(" quan ti ty "+  quantityInStock);
                        if (totalRequestedQuantity > quantityInStock) {
                            throw new BusinessException(CartError.INSUFFICIENT_STOCK,
                                    "Sản phẩm '" + product.getName() + "' (Size: " + itemReq.getSizeName() +
                                            ", Hương vị: " + itemReq.getFlavorName() + ") chỉ còn " +
                                            quantityInStock + " sản phẩm trong kho.");
                        }
                    }
                }
            }

            // 🔹 Kiểm tra item đã tồn tại trong cart để cộng quantity
            boolean itemExists = false;
            for (CartItemEntity item : cart.getItems()) {
                if (item.getProductId().equals(itemReq.getProductId())
                        && ((item.getSizeId() == null && itemReq.getSizeId() == null) || (item.getSizeId() != null && item.getSizeId().equals(itemReq.getSizeId())))
                        && ((item.getFlavorId() == null && itemReq.getFlavorId() == null) || (item.getFlavorId() != null && item.getFlavorId().equals(itemReq.getFlavorId())))
                ) {
                    item.setQuantity(item.getQuantity() + itemReq.getQuantity());
                    itemExists = true;
                    break;
                }
            }

            // 🔹 Thêm item mới
            if (!itemExists) {
                CartItemEntity newItem = CartItemEntity.builder()
                        .productId(itemReq.getProductId())
                        .productName(itemReq.getProductName())
                        .sizeId(itemReq.getSizeId())
                        .sizeName(itemReq.getSizeName())
                        .flavorId(itemReq.getFlavorId())
                        .flavorName(itemReq.getFlavorName())
                        .price(itemReq.getPrice())
                        .quantity(itemReq.getQuantity())
                        .imageUrl(itemReq.getImageUrl())
                        .build();

                cart.getItems().add(newItem);
            }
        }

        // 4️⃣ Tính tổng giá trị cart
        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(total);

        // 5️⃣ Cập nhật thời gian
        cart.setUpdatedAt(Instant.now());

        // 6️⃣ Lưu cart
        CartEntity savedCart = iRepositoryCart.save(cart);

        // 7️⃣ Chuyển sang CreateCartRes để trả về FE
        List<CreateCartRes.CartItemRes> itemsRes = new ArrayList<>();
        for (CartItemEntity item : savedCart.getItems()) {
            itemsRes.add(CreateCartRes.CartItemRes.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .sizeId(item.getSizeId())
                    .sizeName(item.getSizeName())
                    .flavorId(item.getFlavorId())
                    .flavorName(item.getFlavorName())
                    .price(item.getPrice())
                    .quantity(item.getQuantity())
                    .imageUrl(item.getImageUrl())
                    .build());
        }

        return CreateCartRes.builder()
                .cartId(savedCart.getId())
                .userId(savedCart.getUserId())
                .items(itemsRes)
                .totalAmount(savedCart.getTotalAmount())
                .createdAt(savedCart.getCreatedAt())
                .updatedAt(savedCart.getUpdatedAt())
                .build();
    }
}