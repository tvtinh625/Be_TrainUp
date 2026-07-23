package x10.trainup.product.core.usecases.deleteProductUc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import x10.trainup.product.core.repositories.IRepositoryProduct;

@Service
@AllArgsConstructor
public class DeleteProductUcImpl implements IDeleteProductUc {

    private final IRepositoryProduct iRepositoryProduct;

    @Override
    @Transactional
    public void deleteProduct(String productId) {
        // Kiểm tra có tồn tại hay không
        var productOpt = iRepositoryProduct.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại với ID: " + productId);
        }
        iRepositoryProduct.deleteById(productId);
    }
}
