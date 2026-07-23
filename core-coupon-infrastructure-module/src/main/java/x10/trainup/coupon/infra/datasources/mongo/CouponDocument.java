package x10.trainup.coupon.infra.datasources.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import x10.trainup.commons.domain.enums.CouponType;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "coupons")
public class CouponDocument {
    @Id
    private String id;
    private String code;
    private CouponType type;           // PERCENTAGE | FIXED_AMOUNT
    private BigDecimal value;          // 20 (%) hoặc 50000 (VND)
    private BigDecimal minOrderAmount; // Đơn tối thiểu
    private BigDecimal maxDiscount;    // Giảm tối đa
    private Integer usageLimit;        // null = không giới hạn
    private Integer usageCount;        // Đã dùng
    private Instant startDate;
    private Instant endDate;
    private Boolean isActive;
    private Boolean isPublic;

    @CreatedDate
    private Instant createdAt;
}
