package x10.trainup.cart.infra.datasources.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartMongoRepository extends MongoRepository<CartDocument, String> {
    Optional<CartDocument> findByUserId(String userId);
}
