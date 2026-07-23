package x10.trainup.commons.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartEntity {
    private String id;                    // ID của giỏ hàng
    private String userId;                // ID người dùng sở hữu giỏ hàng
    private List<CartItemEntity> items;   // Danh sách sản phẩm trong giỏ
    private BigDecimal totalAmount;       // Tổng giá trị giỏ hàng
    private Instant createdAt;            // Ngày tạo giỏ hàng
    private Instant updatedAt;            // Ngày cập nhật gần nhất
}
