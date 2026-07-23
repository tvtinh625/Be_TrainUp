package x10.trainup.order.infra.datasources.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MongoOrderRepository extends MongoRepository<OrderDocument, String> {
    List<OrderDocument> findByUserId(String userId);
    Optional<OrderDocument> findByOrderNumber(String orderNumber);
    List<OrderDocument> findAllByOrderByCreatedAtDesc();
    List<OrderDocument> findByShippingAddressEmailOrderByCreatedAtDesc(String email);
}