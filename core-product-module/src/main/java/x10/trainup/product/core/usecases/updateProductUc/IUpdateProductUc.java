package x10.trainup.product.core.usecases.updateProductUc;



import x10.trainup.commons.domain.entities.ProductEntity;

public interface IUpdateProductUc {
    ProductEntity process(String productId, UpdateProductReq req);
}