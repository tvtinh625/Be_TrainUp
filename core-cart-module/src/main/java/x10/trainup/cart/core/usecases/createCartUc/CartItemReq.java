package x10.trainup.cart.core.usecases.createCartUc;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemReq {

    @NotBlank(message = "ProductId không được để trống")
    private String productId;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String productName;

    private String sizeId;
    private String sizeName;

    private String flavorId;
    private String flavorName;

    @NotNull(message = "Giá sản phẩm không được để trống")
    private BigDecimal price;

    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    private int quantity;

    private String imageUrl;
}
