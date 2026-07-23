package x10.trainup.cart.core.usecases.deCreaseCartUc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.cart.core.error.CartError;
import x10.trainup.cart.core.repository.IRepositoryCart;
import x10.trainup.commons.domain.entities.CartEntity;
import x10.trainup.commons.domain.entities.CartItemEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.usecases.ICoreUserSerivce;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Iterator;

@Service
@AllArgsConstructor
public class IDecreaseUcImpl implements IDecreaseCartUc {

    private final ICoreUserSerivce iCoreUserSerivce;
    private final IRepositoryCart iRepositoryCart;

    @Override
    public void excute(DecreaseReq req,String userId) {
        // 1️⃣ Kiểm tra user tồn tại
        if (!iCoreUserSerivce.existsById(userId)) {
            throw new BusinessException(UserError.USER_NOT_FOUND, "Không tìm thấy người dùng.");
        }

        // 2️⃣ Lấy giỏ hàng
        CartEntity cart = iRepositoryCart.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(CartError.CART_NOT_FOUND, "Không tìm thấy giỏ hàng."));

        // 3️⃣ Tìm item để giảm
        boolean itemFound = false;
        Iterator<CartItemEntity> iterator = cart.getItems().iterator();
        while (iterator.hasNext()) {
            CartItemEntity item = iterator.next();
            if (item.getProductId().equals(req.getProductId())
                    && ((item.getSizeId() == null && req.getSizeId() == null) || (item.getSizeId() != null && item.getSizeId().equals(req.getSizeId())))
                    && ((item.getFlavorId() == null && req.getFlavorId() == null) || (item.getFlavorId() != null && item.getFlavorId().equals(req.getFlavorId())))
            ) {
                itemFound = true;

                // 4️⃣ Giảm quantity hoặc xóa item nếu = 1
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    iterator.remove(); // xóa item khỏi cart nếu quantity = 1
                }
                break;
            }
        }

        if (!itemFound) {
            throw new BusinessException(CartError.ITEM_NOT_FOUND, "Item không tồn tại trong giỏ hàng.");
        }

        // 5️⃣ Cập nhật tổng giá trị cart
        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(total);

        // 6️⃣ Cập nhật thời gian
        cart.setUpdatedAt(Instant.now());

        // 7️⃣ Lưu cart
        iRepositoryCart.save(cart);
    }
}
