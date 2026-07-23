package x10.trainup.cart.infra.datasources.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDocument {

    private String productId;      // ID sản phẩm
    private String productName;    // Tên sản phẩm
    private String sizeId;         // ID size được chọn
    private String sizeName;       // Tên size (vd: 1serving, 20lb)
    private String flavorId;       // ID hương vị
    private String flavorName;     // Tên hương vị (vd: Socola)
    private BigDecimal price;      // Giá bán tại thời điểm thêm vào giỏ
    private int quantity;          // Số lượng sản phẩm trong giỏ
    private String imageUrl;       // Ảnh đại diện của biến thể
}
