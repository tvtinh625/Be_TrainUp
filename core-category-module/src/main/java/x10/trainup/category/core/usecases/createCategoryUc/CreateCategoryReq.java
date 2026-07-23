package x10.trainup.category.core.usecases.createCategoryUc;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO cho use case: Create Category.
 * Dùng ở tầng UseCase (Core layer), không phụ thuộc framework.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryReq {

    @NotBlank(message = "Tên danh mục không được để trống.")
    @Size(min = 3, max = 100, message = "Tên danh mục phải từ 3 đến 100 ký tự.")
    private String name;

    @Size(max = 255, message = "Mô tả danh mục không được vượt quá 255 ký tự.")
    private String description;

    private boolean active;


    public void normalize() {
        if (name != null) name = name.trim();
        if (description != null) description = description.trim();
    }
    public boolean isValid() {
        return name != null && !name.trim().isEmpty();
    }
}
