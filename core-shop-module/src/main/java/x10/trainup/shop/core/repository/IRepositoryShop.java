package x10.trainup.shop.core.repository;

import x10.trainup.commons.domain.entities.ShopEntity;

import java.util.List;
import java.util.Optional;

public interface IRepositoryShop {

    // Lưu hoặc cập nhật shop
    ShopEntity save(ShopEntity shopEntity);

    // Tìm shop theo ID
    Optional<ShopEntity> findById(String id);

    // Tìm tất cả shops
    List<ShopEntity> findAll();

    // Xóa shop theo ID
    void deleteById(String id);

    // Kiểm tra shop có tồn tại không
    boolean existsById(String id);

    // Tìm shop theo tên
    Optional<ShopEntity> findByName(String name);

    // Kiểm tra tên shop đã tồn tại chưa
    boolean existsByName(String name);
}