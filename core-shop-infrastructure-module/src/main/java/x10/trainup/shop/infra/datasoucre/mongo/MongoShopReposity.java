package x10.trainup.shop.infra.datasoucre.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoShopReposity extends MongoRepository<ShopDocument, String> {

    // Tìm shop theo tên
    Optional<ShopDocument> findByName(String name);

    // Kiểm tra shop có tồn tại theo tên không
    boolean existsByName(String name);

    // Tìm các shop theo province
    // List<ShopDocument> findByAddress_Province(String province);

    // Tìm các shop theo district
    // List<ShopDocument> findByAddress_District(String district);
}