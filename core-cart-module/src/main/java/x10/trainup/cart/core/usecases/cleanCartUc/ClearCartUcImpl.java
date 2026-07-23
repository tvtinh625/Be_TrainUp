package x10.trainup.cart.core.usecases.cleanCartUc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.cart.core.error.CartError;
import x10.trainup.cart.core.repository.IRepositoryCart;
import x10.trainup.commons.domain.entities.CartEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.usecases.ICoreUserSerivce;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClearCartUcImpl implements IClearCartUc {

    private final IRepositoryCart iRepositoryCart;
    private final ICoreUserSerivce iCoreUserSerivce;

    @Override
    public void execute(String userId) {
        // 1. Validate user tồn tại
        if (!iCoreUserSerivce.existsById(userId)) {
            throw new BusinessException(UserError.USER_NOT_FOUND, "Không tìm thấy người dùng.");
        }
        Optional<CartEntity> cartOptional = iRepositoryCart.findByUserId(userId);
        if (cartOptional.isEmpty()) {
            throw new BusinessException(
                    CartError.CART_NOT_FOUND,
                    "Giỏ hàng Trống"
            );
        }
        CartEntity cart = cartOptional.get();
        cart.setItems(new ArrayList<>());
        cart.setTotalAmount(BigDecimal.ZERO);
        cart.setUpdatedAt(Instant.now());

        iRepositoryCart.save(cart);
    }
}