package x10.trainup.cart.core.usecases.deCreaseCartUc;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class DecreaseReq {


    @NotBlank(message = "ProductId không được để trống")
    private String productId;

    @NotBlank(message = "SizeId không được để trống")
    private String sizeId;   // Bắt buộc luôn có size

    @NotBlank(message = "flavorId không được để trống ")
    private String flavorId; // Có thể null nếu không có hương vị
}
