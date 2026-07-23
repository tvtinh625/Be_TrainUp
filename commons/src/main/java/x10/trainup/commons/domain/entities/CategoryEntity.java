package x10.trainup.commons.domain.entities;

import lombok.*;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryEntity {
    private String id;
    private String name;
    private String description;
    private boolean active; // ✅ Thêm thuộc tính trạng thái danh mục
}
