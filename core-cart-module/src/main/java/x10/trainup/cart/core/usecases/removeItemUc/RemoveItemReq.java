package x10.trainup.cart.core.usecases.removeItemUc;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemoveItemReq {

    @NotBlank(message = "productId không được để trống")
    private String productId;

    @NotBlank(message = "sizeId không được để trống")
    private String sizeId;

    @NotBlank(message = "flavorId không được để trống")
    private String flavorId;
}