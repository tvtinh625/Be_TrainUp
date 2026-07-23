package x10.trainup.commons.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
    private String id;               // ID sản phẩm
    private String name;             // Tên sản phẩm
    private String description;      // Mô tả chung
    private String categoryId;       // Loại / danh mục
    private String brand;            // Thương hiệu
    private Instant createdAt;
    private Instant updatedAt;
    private List<SizeVariantEntity> sizes; // Các size + hương
}
