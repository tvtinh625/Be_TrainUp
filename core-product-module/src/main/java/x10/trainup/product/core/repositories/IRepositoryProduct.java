package x10.trainup.product.core.repositories;

import x10.trainup.commons.domain.entities.ProductEntity;

import java.util.List;
import java.util.Optional;

public interface IRepositoryProduct {

    ProductEntity save(ProductEntity productEntity);

    Optional<ProductEntity> findById(String id);

    List<ProductEntity> findAll();

    void deleteById(String id);

    /**
     * Tìm sản phẩm có tên chứa chuỗi (không phân biệt hoa thường)
     * => phục vụ check trùng tên khi tạo mới
     */
    List<ProductEntity> findByNameContainingIgnoreCase(String name);

    /**
     * Tìm sản phẩm theo categoryId
     */
    List<ProductEntity> findByCategoryId(String categoryId);

    boolean existsByCategoryId(String categoryId);


    boolean existsSize(String productId,String sizeId);

    boolean existsFlavor(String productId, String sizeId, String flavorId);

}
