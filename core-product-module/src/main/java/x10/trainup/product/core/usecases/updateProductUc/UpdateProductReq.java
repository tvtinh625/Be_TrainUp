package x10.trainup.product.core.usecases.updateProductUc;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductReq {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 3, max = 255, message = "Tên sản phẩm phải từ 3-255 ký tự")
    private String name;

    @NotBlank(message = "Mô tả sản phẩm không được để trống")
    @Size(min = 10, max = 5000, message = "Mô tả phải từ 10-5000 ký tự")
    private String description;

    @NotBlank(message = "ID danh mục không được để trống")
    private String categoryId;

    @NotBlank(message = "Thương hiệu không được để trống")
    @Size(min = 2, max = 100, message = "Thương hiệu phải từ 2-100 ký tự")
    private String brand;

    private Boolean active;

    @NotNull(message = "Danh sách sizes không được null")
    @NotEmpty(message = "Sản phẩm phải có ít nhất 1 size")
    @Size(max = 20, message = "Tối đa 20 sizes cho mỗi sản phẩm")
    @Valid
    private List<UpdateSizeVariantReq> sizes;
}