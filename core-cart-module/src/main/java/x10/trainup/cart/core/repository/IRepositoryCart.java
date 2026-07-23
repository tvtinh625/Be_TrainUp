package x10.trainup.cart.core.repository;

import x10.trainup.commons.domain.entities.CartEntity;

import java.util.Optional;

public interface IRepositoryCart {

    // 🛒 Lưu giỏ hàng (tạo mới hoặc update)
    CartEntity save(CartEntity cart);

    // 🔍 Tìm giỏ hàng theo ID
    Optional<CartEntity> findById(String cartId);

    // 🔍 Tìm giỏ hàng theo User ID
    Optional<CartEntity> findByUserId(String userId);

    // ❌ Xóa giỏ hàng theo ID
    void deleteById(String cartId);

    CartEntity createEmptyCart(String userId);
}
