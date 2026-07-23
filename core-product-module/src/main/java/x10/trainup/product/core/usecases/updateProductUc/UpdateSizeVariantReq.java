package x10.trainup.product.core.usecases.updateProductUc;



import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSizeVariantReq {

    private String id;

    @NotBlank(message = "Size không được để trống")
    @Size(min = 1, max = 50, message = "Size phải từ 1-50 ký tự")
    @Pattern(
            regexp = "^[1-9][0-9]*\\s*(serving|servings|Serving|Servings)$",
            message = "Size phải theo định dạng hợp lệ (vd: 1 serving, 2 servings)"
    )
    private String size;
    @NotNull(message = "Giá không được null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    @Digits(integer = 10, fraction = 2, message = "Giá không đúng định dạng")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = false, message = "Giá giảm phải lớn hơn 0")
    @Digits(integer = 10, fraction = 2, message = "Giá giảm không đúng định dạng")
    private BigDecimal discountPrice;

    @Size(max = 500, message = "URL ảnh không được vượt quá 500 ký tự")
    private String imageUrl;

    @Size(max = 4, message = "Tối đa 4 ảnh phụ")
    private List<@Size(max = 500) String> imageUrls;

    @Size(max = 50, message = "Trọng lượng không được vượt quá 50 ký tự")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?(kg|g|lb|oz)?$",
            message = "Trọng lượng phải theo định dạng hợp lệ (vd: 4.5kg)")
    private String weight;

    @Valid
    @Size(max = 50, message = "Tối đa 50 hương vị cho mỗi size")
    private List<UpdateFlavorVariantReq> flavors;
}