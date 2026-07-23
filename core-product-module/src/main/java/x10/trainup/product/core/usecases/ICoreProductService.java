package x10.trainup.product.core.usecases;

import x10.trainup.commons.domain.entities.ProductEntity;
import x10.trainup.product.core.usecases.createProductUc.CreateProductReq;
import x10.trainup.product.core.usecases.updateProductUc.UpdateProductReq;

import java.util.List;
import java.util.Optional;

public interface ICoreProductService {

    ProductEntity createProduct(CreateProductReq product);

    List<ProductEntity> getAllProducts();

    Optional<ProductEntity> findProductById(String id);

    List<ProductEntity> findProductsByCategoryId(String categoryId);

    List<ProductEntity> searchProductsByName(String name);

    ProductEntity updateProduct(String productId, UpdateProductReq req);

    void deleteProduct(String productId);

}
