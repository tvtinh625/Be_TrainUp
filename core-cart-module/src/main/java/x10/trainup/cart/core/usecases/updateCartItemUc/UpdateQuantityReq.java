package x10.trainup.cart.core.usecases.updateCartItemUc;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuantityReq {


    @NotBlank(message = "productId không được để trống")
    private String productId;

    @NotBlank(message = "sizeId không được để trống")
    private String sizeId;

    @NotBlank(message = "flavorId không được để trống")
    private String flavorId;

    @Min(value = 1, message = "Số lượng phải >= 1")
    private int quantity; // Số lượng mới (set trực tiếp)
}