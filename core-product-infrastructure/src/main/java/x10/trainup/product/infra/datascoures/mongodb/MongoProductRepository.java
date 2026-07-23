package x10.trainup.product.infra.datascoures.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import x10.trainup.product.infra.datascoures.mongodb.ProductDocument;

import java.util.List;

@Repository
public interface MongoProductRepository extends MongoRepository<ProductDocument, String> {

    // Tìm sản phẩm theo tên (bỏ qua hoa/thường)
    List<ProductDocument> findByNameContainingIgnoreCase(String name);

    // Tìm theo categoryId
    List<ProductDocument> findByCategoryId(String categoryId);

    boolean existsByCategoryId(String categoryId);

}
