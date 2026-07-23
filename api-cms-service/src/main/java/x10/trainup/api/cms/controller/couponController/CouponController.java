package x10.trainup.api.cms.controller.couponController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import x10.trainup.commons.domain.entities.CouponEntity;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.coupon.core.usecases.ICouponService;

import java.util.List;

@RestController
@RequestMapping("api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final ICouponService couponService;

    @PostMapping
    public ResponseEntity<ApiResponse<CouponEntity>> createCoupon(@RequestBody CouponEntity coupon) {
        CouponEntity created = couponService.createCoupon(coupon);
        return ResponseEntity.ok(ApiResponse.of(200, "SUCCESS", "Tạo mã khuyến mãi thành công", created, null, null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponEntity>> updateCoupon(@PathVariable String id, @RequestBody CouponEntity coupon) {
        CouponEntity updated = couponService.updateCoupon(id, coupon);
        return ResponseEntity.ok(ApiResponse.of(200, "SUCCESS", "Cập nhật mã khuyến mãi thành công", updated, null, null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponEntity>>> getAllCoupons() {
        List<CouponEntity> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(ApiResponse.of(200, "SUCCESS", "Lấy danh sách thành công", coupons, null, null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponEntity>> getCouponById(@PathVariable String id) {
        CouponEntity coupon = couponService.getCouponById(id);
        return ResponseEntity.ok(ApiResponse.of(200, "SUCCESS", "Thành công", coupon, null, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.of(200, "SUCCESS", "Xóa thành công", null, null, null));
    }
}
