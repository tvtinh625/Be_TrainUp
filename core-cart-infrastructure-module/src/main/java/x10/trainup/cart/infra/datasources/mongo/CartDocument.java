package x10.trainup.cart.infra.datasources.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "carts") // 🧺 Tên collection trong MongoDB
public class CartDocument {

    @Id
    private String id;                     // ID của giỏ hàng
    private String userId;                 // ID người dùng sở hữu giỏ hàng
    private List<CartItemDocument> items;  // Danh sách sản phẩm trong giỏ
    private BigDecimal totalAmount;        // Tổng giá trị giỏ hàng
    private Instant createdAt;             // Ngày tạo giỏ hàng
    private Instant updatedAt;             // Ngày cập nhật gần nhất
}
