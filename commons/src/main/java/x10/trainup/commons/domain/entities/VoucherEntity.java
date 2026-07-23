package x10.trainup.commons.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoucherEntity {
    private String id;
    private String code;             // Mã voucher
    private BigDecimal discount;     // Giá trị giảm (có thể là tiền hoặc %)
    private boolean isPercentage;    // true nếu là %, false nếu là tiền
    private Instant startAt;   // thời gian bắt đầu áp dụng
    private Instant endAt;     // thời gian hết hạn
    private int usageLimit;          // số lần được sử dụng
    private int usedCount;           // số lần đã dùng
    private boolean active;
}
