package x10.trainup.cart.core.usecases.RemoveListItemsUC;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import x10.trainup.cart.core.error.CartError;
import x10.trainup.cart.core.repository.IRepositoryCart;
import x10.trainup.commons.domain.entities.CartEntity;
import x10.trainup.commons.domain.entities.CartItemEntity;
import x10.trainup.commons.exceptions.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RemoveListItemsImpl implements IRemoveListItemsUc {

    private final IRepositoryCart iRepositoryCart;

    @Override
    @Transactional
    public void execute(RemoveListItemsReq req, String userId) {

        // 1. Lấy giỏ hàng user
        Optional<CartEntity> cartOptional = iRepositoryCart.findByUserId(userId);

        if (cartOptional.isEmpty()) {
            throw new BusinessException(
                    CartError.CART_NOT_FOUND,
                    "Không tìm thấy giỏ hàng của userId: " + userId
            );
        }

        CartEntity cart = cartOptional.get();

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BusinessException(
                    CartError.CART_IS_EMPTY,
                    "Giỏ hàng đang trống, không thể xóa danh sách sản phẩm"
            );
        }

        List<CartItemEntity> currentItems = cart.getItems();

        // 2. Thực hiện xóa: giữ lại item KHÔNG NẰM TRONG DANH SÁCH cần xóa
        List<CartItemEntity> updatedItems = currentItems.stream()
                .filter(item -> !isInRemoveList(item, req))
                .collect(Collectors.toList());

        // 3. Nếu không có item nào bị xóa
        if (updatedItems.size() == currentItems.size()) {
            throw new BusinessException(
                    CartError.ITEM_NOT_FOUND,
                    "Không tìm thấy bất kỳ sản phẩm nào trong danh sách cần xóa"
            );
        }

        // 4. Cập nhật giỏ hàng
        cart.setItems(updatedItems);

        // 5. Tính lại tổng tiền
        BigDecimal newTotalAmount = updatedItems.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(newTotalAmount);
        cart.setUpdatedAt(Instant.now());

        // 6. Lưu lại
        iRepositoryCart.save(cart);
    }

    private boolean isInRemoveList(CartItemEntity item, RemoveListItemsReq req) {
        return req.getItems().stream().anyMatch(toRemove ->
                item.getProductId().equals(toRemove.getProductId()) &&
                        item.getSizeId().equals(toRemove.getSizeId()) &&
                        item.getFlavorId().equals(toRemove.getFlavorId())
        );
    }
}
