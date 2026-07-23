package x10.trainup.cart.core.usecases.getCartUc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import x10.trainup.commons.domain.entities.CartItemEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCartRes {
    private String cartId;                    // ID giỏ hàng
    private String userId;                    // ID user
    private List<CartItemEntity> items;       // Danh sách sản phẩm
    private BigDecimal totalAmount;           // Tổng tiền
    private int totalItems;                   // Tổng số lượng items
    private Instant createdAt;                // Thời gian tạo
    private Instant updatedAt;                // Thời gian cập nhật
    private String message;                   // "Giỏ hàng trống" hoặc thông báo khác
    private boolean isEmpty;                  // true nếu giỏ hàng không có gì
}