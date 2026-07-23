package x10.trainup.coupon.core.repositories;

import x10.trainup.commons.domain.entities.CouponEntity;
import java.util.List;
import java.util.Optional;

public interface ICouponRepository {
    CouponEntity save(CouponEntity coupon);
    Optional<CouponEntity> findById(String id);
    Optional<CouponEntity> findByCode(String code);
    List<CouponEntity> findAll();
    void deleteById(String id);
}
