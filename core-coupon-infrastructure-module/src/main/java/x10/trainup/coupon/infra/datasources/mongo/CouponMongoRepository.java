package x10.trainup.coupon.infra.datasources.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponMongoRepository extends MongoRepository<CouponDocument, String> {
    Optional<CouponDocument> findByCode(String code);
}
