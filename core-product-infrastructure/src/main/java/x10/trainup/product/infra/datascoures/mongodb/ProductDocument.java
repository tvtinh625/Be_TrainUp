package x10.trainup.product.infra.datascoures.mongodb;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class ProductDocument {

    @Id
    private String id;
    private String name;             // Tên sản phẩm
    private String description;      // Mô tả
    private String categoryId;       // ID danh mục
    private String brand;            // Thương hiệu
    private boolean active;          // Còn bán hay không
    private Instant createdAt;
    private Instant updatedAt;

    private List<SizeVariantDocument> sizes; // Danh sách các size (có hương vị bên trong)
}
