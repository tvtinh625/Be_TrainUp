package x10.trainup.commons.domain.entities;

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
public class SizeVariantEntity {
    private String id;               // ID riêng cho size
    private String size;             // Size (10lb, 20lb…)
    private BigDecimal price;        // Giá theo size
    private BigDecimal discountPrice;// Giá giảm theo size
    private String imageUrl;         // Hình ảnh size
    private List<String> imageUrls;  // Ảnh phụ size
    private String weight;           // Trọng lượng thực tế của size, ví dụ "4.5kg"
    private List<FlavorVariantEntity> flavors; // Hương vị + stock
}
