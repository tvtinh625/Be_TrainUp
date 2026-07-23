package x10.trainup.coupon.core.usecases;

import x10.trainup.commons.domain.entities.CouponEntity;

import java.math.BigDecimal;
import java.util.List;

public interface ICouponService {
    CouponEntity createCoupon(CouponEntity coupon);
    CouponEntity updateCoupon(String id, CouponEntity coupon);
    CouponEntity getCouponById(String id);
    CouponEntity getCouponByCode(String code);
    List<CouponEntity> getAllCoupons();
    void deleteCoupon(String id);
    
    // Core logic
    BigDecimal calculateDiscount(String code, BigDecimal orderSubtotal);
    void markCouponUsed(String code);
}
