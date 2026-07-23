package x10.trainup.product.core.usecases;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.category.core.repositories.ICategoryReposity;
import x10.trainup.commons.domain.entities.ProductEntity;
import x10.trainup.product.core.repositories.IRepositoryProduct;
import x10.trainup.product.core.usecases.createProductUc.CreateProductReq;
import x10.trainup.product.core.usecases.createProductUc.ICreateProductUc;
import x10.trainup.product.core.usecases.deleteProductUc.IDeleteProductUc;
import x10.trainup.product.core.usecases.updateProductUc.IUpdateProductUc;
import x10.trainup.product.core.usecases.updateProductUc.UpdateProductReq;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CoreProductServiceImpl implements  ICoreProductService{

    private final ICreateProductUc createProductUc;
    private final IRepositoryProduct repositoryProduct;
    private final IUpdateProductUc updateProductUc;
    private  final IDeleteProductUc iDeleteProductUc;

    @Override
    public ProductEntity createProduct(CreateProductReq product) {
        return createProductUc.process(product);
    }

    @Override
    public List<ProductEntity> getAllProducts() {
        return repositoryProduct.findAll();
    }

    @Override
    public Optional<ProductEntity> findProductById(String id) {
            return repositoryProduct.findById(id);
    }

    @Override
    public List<ProductEntity> findProductsByCategoryId(String categoryId) {
        return repositoryProduct.findByCategoryId(categoryId);
    }


    @Override
    public List<ProductEntity> searchProductsByName(String name) {
        return repositoryProduct.findByNameContainingIgnoreCase(name);
    }

    @Override
    public ProductEntity updateProduct(String productId, UpdateProductReq req) {
        return updateProductUc.process(productId, req);
    }

    @Override
    public void deleteProduct (String productId){
            iDeleteProductUc.deleteProduct(productId);
    }
}
