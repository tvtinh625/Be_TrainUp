package x10.trainup.coupon.infra.adapters.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import x10.trainup.commons.domain.entities.CouponEntity;
import x10.trainup.coupon.core.repositories.ICouponRepository;
import x10.trainup.coupon.infra.adapters.repositories.mapper.CouponMapper;
import x10.trainup.coupon.infra.datasources.mongo.CouponDocument;
import x10.trainup.coupon.infra.datasources.mongo.CouponMongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CouponRepositoryAdapter implements ICouponRepository {

    private final CouponMongoRepository mongoRepository;

    @Override
    public CouponEntity save(CouponEntity coupon) {
        CouponDocument doc = CouponMapper.toDocument(coupon);
        CouponDocument saved = mongoRepository.save(doc);
        return CouponMapper.toEntity(saved);
    }

    @Override
    public Optional<CouponEntity> findById(String id) {
        return mongoRepository.findById(id).map(CouponMapper::toEntity);
    }

    @Override
    public Optional<CouponEntity> findByCode(String code) {
        return mongoRepository.findByCode(code).map(CouponMapper::toEntity);
    }

    @Override
    public List<CouponEntity> findAll() {
        return mongoRepository.findAll().stream()
                .map(CouponMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }
}
