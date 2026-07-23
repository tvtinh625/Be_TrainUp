// ...existing code...
package x10.trainup.category.core.usecases.updateCategoryUc;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request DTO cho use case: Update Category.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCategoryReq {


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
}

