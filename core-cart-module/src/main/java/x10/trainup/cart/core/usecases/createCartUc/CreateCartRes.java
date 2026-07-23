package x10.trainup.cart.core.usecases.createCartUc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;



@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateCartRes {

    private String cartId;          // ID giỏ hàng
    private String userId;          // ID người dùng
    private List<CartItemRes> items; // Danh sách sản phẩm trong cart
    private BigDecimal totalAmount; // Tổng giá trị giỏ hàng
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public  static class CartItemRes {
        private String productId;
        private String productName;
        private String sizeId;
        private String sizeName;
        private String flavorId;
        private String flavorName;
        private BigDecimal price;
        private int quantity;
        private String imageUrl;
    }
}
