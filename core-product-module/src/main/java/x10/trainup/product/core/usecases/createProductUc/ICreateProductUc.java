package x10.trainup.product.core.usecases.createProductUc;

import x10.trainup.commons.domain.entities.ProductEntity;

public interface ICreateProductUc {
    ProductEntity process(CreateProductReq req);
}
