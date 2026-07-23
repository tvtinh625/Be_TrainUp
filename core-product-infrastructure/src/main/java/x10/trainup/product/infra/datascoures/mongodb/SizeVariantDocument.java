package x10.trainup.product.infra.datascoures.mongodb;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SizeVariantDocument {

    private String id;               // ID riêng cho size
    private String size;             // Size (5lb, 10lb,…)

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;        // Giá theo size

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal discountPrice;// Giá giảm
    private String imageUrl;         // Hình ảnh chính cho size
    private List<String> imageUrls;  // Ảnh phụ
    private String weight;           // Trọng lượng
    private List<FlavorVariantDocument> flavors; // Danh sách hương vị
}
