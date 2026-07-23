package x10.trainup.cart.core.usecases.getCartUc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.cart.core.repository.IRepositoryCart;
import x10.trainup.commons.domain.entities.CartEntity;
import x10.trainup.commons.domain.entities.CartItemEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.usecases.ICoreUserSerivce;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class IGetCartUcImpl implements IGerCartUc {

    private final ICoreUserSerivce iCoreUserSerivce;
    private final IRepositoryCart iRepositoryCart;

    @Override
    public GetCartRes excute(String userId) {
        // 1. Validate userId có tồn tại không
        if (!iCoreUserSerivce.existsById(userId)) {
            throw new BusinessException(UserError.USER_NOT_FOUND, "Không tìm thấy người dùng.");
        }

        // 2. Lấy giỏ hàng từ repository
        Optional<CartEntity> cartOptional = iRepositoryCart.findByUserId(userId);

        // 3. Nếu giỏ hàng chưa tồn tại → Tạo giỏ hàng mới (rỗng)
        if (cartOptional.isEmpty()) {
            CartEntity newCart = CartEntity.builder()
                    .userId(userId)
                    .items(new ArrayList<>())
                    .totalAmount(BigDecimal.ZERO)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            CartEntity savedCart = iRepositoryCart.save(newCart);

            return mapToResponse(savedCart, true, "Giỏ hàng trống");
        }

        // 4. Nếu giỏ hàng đã tồn tại → Trả về thông tin
        CartEntity cart = cartOptional.get();
        boolean isEmpty = cart.getItems() == null || cart.getItems().isEmpty();
        String message = isEmpty ? "Giỏ hàng trống" : null;

        return mapToResponse(cart, isEmpty, message);
    }

    /**
     * Map CartEntity sang GetCartRes
     */
    private GetCartRes mapToResponse(CartEntity cart, boolean isEmpty, String message) {
        int totalItems = 0;
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            totalItems = cart.getItems().stream()
                    .mapToInt(CartItemEntity::getQuantity)
                    .sum();
        }

        return GetCartRes.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(cart.getItems() != null ? cart.getItems() : new ArrayList<>())
                .totalAmount(cart.getTotalAmount() != null ? cart.getTotalAmount() : BigDecimal.ZERO)
                .totalItems(totalItems)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .isEmpty(isEmpty)
                .message(message)
                .build();
    }
}