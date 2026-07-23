package x10.trainup.cart.core.usecases.removeItemUc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.cart.core.error.CartError;
import x10.trainup.cart.core.repository.IRepositoryCart;
import x10.trainup.commons.domain.entities.CartEntity;
import x10.trainup.commons.domain.entities.CartItemEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.user.core.usecases.ICoreUserSerivce;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RemoveItemUcImpl implements IRemoveItemUc {

    private final IRepositoryCart iRepositoryCart;
    private final ICoreUserSerivce iCoreUserSerivce;

    @Override
    public void execute(RemoveItemReq req,String userId) {
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
                    "Không tìm thấy giỏ hàng của người dùng: " +userId
            );
        }

        CartEntity cart = cartOptional.get();

        // 3. Kiểm tra giỏ hàng có rỗng không
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessException(
                    CartError.CART_IS_EMPTY,
                    "Giỏ hàng trống, không có sản phẩm để xóa"
            );
        }

        // 4. Tìm và xóa item khớp với productId + sizeId + flavorId
        List<CartItemEntity> updatedItems = cart.getItems().stream()
                .filter(item -> !isMatchingItem(item, req))
                .collect(Collectors.toList());

        // 5. Kiểm tra xem có item nào bị xóa không
        if (updatedItems.size() == cart.getItems().size()) {
            throw new BusinessException(
                    CartError.ITEM_NOT_FOUND,
                    String.format("Không tìm thấy sản phẩm trong giỏ hàng: productId=%s, sizeId=%s, flavorId=%s",
                            req.getProductId(), req.getSizeId(), req.getFlavorId())
            );
        }

        // 6. Cập nhật lại giỏ hàng
        cart.setItems(updatedItems);

        // 7. Tính lại tổng tiền
        BigDecimal newTotalAmount = updatedItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(newTotalAmount);
        cart.setUpdatedAt(Instant.now());

        // 8. Lưu vào database
        iRepositoryCart.save(cart);
    }

    /**
     * Kiểm tra item có khớp với điều kiện xóa không
     */
    private boolean isMatchingItem(CartItemEntity item, RemoveItemReq req) {
        return item.getProductId().equals(req.getProductId())
                && item.getSizeId().equals(req.getSizeId())
                && item.getFlavorId().equals(req.getFlavorId());
    }
}