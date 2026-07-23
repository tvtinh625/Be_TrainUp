package x10.trainup.cart.core.usecases.updateCartItemUc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.cart.core.error.CartError;
import x10.trainup.cart.core.repository.IRepositoryCart;
import x10.trainup.commons.domain.entities.CartEntity;
import x10.trainup.commons.domain.entities.CartItemEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.product.core.repositories.IRepositoryProduct;
import x10.trainup.user.core.usecases.ICoreUserSerivce;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UpdateQuantityUcImpl implements IUpdateQuantityUc {

    private final IRepositoryCart iRepositoryCart;
    private final ICoreUserSerivce iCoreUserSerivce;
    private final IRepositoryProduct iRepositoryProduct;

    @Override
    public void execute(UpdateQuantityReq req, String userId) {
        // 1. Validate user tồn tại
        if (!iCoreUserSerivce.existsById(userId)) {
            throw new BusinessException(
                    CartError.USER_NOT_FOUND,
                    "Không tìm thấy người dùng với id: " + userId
            );
        }

        // 2. Tìm giỏ hàng của user
        Optional<CartEntity> cartOptional = iRepositoryCart.findByUserId(userId);

        if (cartOptional.isEmpty()) {
            throw new BusinessException(
                    CartError.CART_NOT_FOUND,
                    "Không tìm thấy giỏ hàng của người dùng: " + userId
            );
        }

        CartEntity cart = cartOptional.get();

        // 3. Kiểm tra giỏ hàng có rỗng không
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessException(
                    CartError.CART_IS_EMPTY,
                    "Giỏ hàng trống, không có sản phẩm để cập nhật"
            );
        }

        // 4. Tìm item cần cập nhật
        CartItemEntity itemToUpdate = cart.getItems().stream()
                .filter(item -> isMatchingItem(item, req))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        CartError.ITEM_NOT_FOUND,
                        String.format("Không tìm thấy sản phẩm trong giỏ hàng: productId=%s, sizeId=%s, flavorId=%s",
                                req.getProductId(), req.getSizeId(), req.getFlavorId())
                ));

        // 5. Kiểm tra số lượng tồn kho trước khi cập nhật
        if (req.getFlavorId() != null && req.getSizeId() != null) {
            // Lấy thông tin product
            var productOpt = iRepositoryProduct.findById(req.getProductId());
            if (!productOpt.isPresent()) {
                throw new BusinessException(CartError.PRODUCT_NOT_FOUND,
                        "ProductId: " + req.getProductId() + " không tồn tại.");
            }
            var product = productOpt.get();

            // Lấy thông tin flavor từ product
            var sizeOpt = product.getSizes().stream()
                    .filter(s -> s.getId().equals(req.getSizeId()))
                    .findFirst();

            if (sizeOpt.isPresent()) {
                var flavorOpt = sizeOpt.get().getFlavors().stream()
                        .filter(f -> f.getId().equals(req.getFlavorId()))
                        .findFirst();

                if (flavorOpt.isPresent()) {
                    var flavor = flavorOpt.get();

                    // Kiểm tra flavor có đang active không
                    if (!flavor.isActive()) {
                        throw new BusinessException(CartError.PRODUCT_INACTIVE,
                                "Sản phẩm '" + product.getName() + "' với hương vị này đã ngưng kinh doanh.");
                    }

                    // Kiểm tra số lượng mới có vượt quá tồn kho không
                    int quantityInStock = flavor.getQuantityInStock();

                    if (req.getQuantity() > quantityInStock) {
                        throw new BusinessException(CartError.INSUFFICIENT_STOCK,
                                "Sản phẩm '" + product.getName() + "' chỉ còn " +
                                        quantityInStock + " sản phẩm trong kho. " +
                                        "Không thể cập nhật số lượng thành " + req.getQuantity() + ".");
                    }
                }
            }
        }

        // 6. Cập nhật số lượng mới
        itemToUpdate.setQuantity(req.getQuantity());

        // 7. Tính lại tổng tiền
        BigDecimal newTotalAmount = cart.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(newTotalAmount);
        cart.setUpdatedAt(Instant.now());

        // 8. Lưu vào database
        iRepositoryCart.save(cart);
    }

    /**
     * Kiểm tra item có khớp với điều kiện không
     */
    private boolean isMatchingItem(CartItemEntity item, UpdateQuantityReq req) {
        return item.getProductId().equals(req.getProductId())
                && item.getSizeId().equals(req.getSizeId())
                && item.getFlavorId().equals(req.getFlavorId());
    }
}