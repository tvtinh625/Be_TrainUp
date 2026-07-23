package x10.trainup.coupon.core.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.entities.CouponEntity;
import x10.trainup.commons.domain.enums.CouponType;
import x10.trainup.coupon.core.repositories.ICouponRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements ICouponService {

    private final ICouponRepository couponRepository;

    @Override
    public CouponEntity createCoupon(CouponEntity coupon) {
        if (coupon.getUsageCount() == null) {
            coupon.setUsageCount(0);
        }
        if (coupon.getIsActive() == null) {
            coupon.setIsActive(true);
        }
        if (coupon.getIsPublic() == null) {
            coupon.setIsPublic(true);
        }
        return couponRepository.save(coupon);
    }

    @Override
    public CouponEntity updateCoupon(String id, CouponEntity coupon) {
        CouponEntity existing = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        existing.setCode(coupon.getCode());
        existing.setType(coupon.getType());
        existing.setValue(coupon.getValue());
        existing.setMinOrderAmount(coupon.getMinOrderAmount());
        existing.setMaxDiscount(coupon.getMaxDiscount());
        existing.setUsageLimit(coupon.getUsageLimit());
        existing.setStartDate(coupon.getStartDate());
        existing.setEndDate(coupon.getEndDate());
        existing.setIsActive(coupon.getIsActive());
        if (coupon.getIsPublic() != null) {
            existing.setIsPublic(coupon.getIsPublic());
        } else {
            existing.setIsPublic(true); // Mặc định là true nếu không gửi
        }

        return couponRepository.save(existing);
    }

    @Override
    public CouponEntity getCouponById(String id) {
        return couponRepository.findById(id).orElse(null);
    }

    @Override
    public CouponEntity getCouponByCode(String code) {
        return couponRepository.findByCode(code).orElse(null);
    }

    @Override
    public List<CouponEntity> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    public void deleteCoupon(String id) {
        couponRepository.deleteById(id);
    }

    @Override
    public BigDecimal calculateDiscount(String code, BigDecimal orderSubtotal) {
        CouponEntity coupon = getCouponByCode(code);
        if (coupon == null || !Boolean.TRUE.equals(coupon.getIsActive())) {
            throw new RuntimeException("Mã giảm giá không tồn tại hoặc đã bị vô hiệu hóa.");
        }

        Instant now = Instant.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new RuntimeException("Mã giảm giá chưa đến thời gian áp dụng.");
        }
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn.");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng.");
        }

        if (coupon.getMinOrderAmount() != null && orderSubtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã.");
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getType() == CouponType.FIXED_AMOUNT) {
            discount = coupon.getValue();
        } else if (coupon.getType() == CouponType.PERCENTAGE) {
            BigDecimal percent = coupon.getValue().divide(BigDecimal.valueOf(100));
            discount = orderSubtotal.multiply(percent);
            
            if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                discount = coupon.getMaxDiscount();
            }
        }

        // Không được giảm quá tổng tiền
        if (discount.compareTo(orderSubtotal) > 0) {
            discount = orderSubtotal;
        }

        return discount;
    }

    @Override
    public void markCouponUsed(String code) {
        CouponEntity coupon = getCouponByCode(code);
        if (coupon != null) {
            coupon.setUsageCount(coupon.getUsageCount() + 1);
            couponRepository.save(coupon);
        }
    }
}
