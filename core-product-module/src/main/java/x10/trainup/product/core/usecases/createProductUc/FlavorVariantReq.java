package x10.trainup.product.core.usecases.createProductUc;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlavorVariantReq {

    @NotBlank(message = "Tên hương vị không được để trống")
    @Size(min = 2, max = 100, message = "Tên hương vị phải từ 2-100 ký tự")
    private String flavor;

    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    @Max(value = 1000000, message = "Số lượng tồn kho không hợp lệ")
    private Integer quantityInStock = 0;

    private boolean active = true;
}