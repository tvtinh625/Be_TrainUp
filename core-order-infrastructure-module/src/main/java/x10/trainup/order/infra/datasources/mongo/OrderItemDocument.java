package x10.trainup.order.infra.datasources.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDocument {

    private String productId;      // ID sản phẩm
    private String productName;    // Tên sản phẩm (snapshot)
    private String sizeId;         // ID size
    private String sizeName;       // Tên size
    private String flavorId;       // ID flavor
    private String flavorName;     // Tên flavor
    private BigDecimal price;      // Giá tại thời điểm đặt
    private int quantity;          // Số lượng
    private BigDecimal subtotal;   // = price * quantity
    private String imageUrl;       // Ảnh sản phẩm
}