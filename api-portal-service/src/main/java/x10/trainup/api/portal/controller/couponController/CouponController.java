package x10.trainup.api.portal.controller.couponController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.coupon.core.usecases.ICouponService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final ICouponService couponService;

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal orderSubtotal) {
        try {
            BigDecimal discount = couponService.calculateDiscount(code, orderSubtotal);
            return ResponseEntity.ok(ApiResponse.of(200, "SUCCESS", "Mã hợp lệ", Map.of("discountAmount", discount), null, null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.of(400, "INVALID_COUPON", e.getMessage(), null, null, null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<x10.trainup.commons.domain.entities.CouponEntity>>> getAvailableCoupons() {
        // Trong thực tế có thể filter theo active, date. Hiện tại trả về tất cả
        java.util.List<x10.trainup.commons.domain.entities.CouponEntity> coupons = couponService.getAllCoupons();
        
        // Chỉ trả về các mã còn hạn, đang active và PUBLIC
        java.util.List<x10.trainup.commons.domain.entities.CouponEntity> available = coupons.stream()
            .filter(c -> c.getIsActive() && 
                         (c.getIsPublic() != null ? c.getIsPublic() : true) &&
                         (c.getEndDate() == null || c.getEndDate().isAfter(java.time.Instant.now())) &&
                         (c.getUsageLimit() == null || c.getUsageCount() < c.getUsageLimit()))
            .toList();

        return ResponseEntity.ok(ApiResponse.of(200, "SUCCESS", "Thành công", available, null, null));
    }
}
