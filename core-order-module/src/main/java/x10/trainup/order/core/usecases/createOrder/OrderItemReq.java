package x10.trainup.order.core.usecases.createOrder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemReq {

    @NotBlank(message = "ID sản phẩm không được để trống")
    private String productId;

    @NotBlank(message = "ID size không được để trống")
    private String sizeId;

    @NotBlank(message = "ID hương vị không được để trống")
    private String flavorId;

    @NotNull(message = "Số lượng là bắt buộc")
    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
}
