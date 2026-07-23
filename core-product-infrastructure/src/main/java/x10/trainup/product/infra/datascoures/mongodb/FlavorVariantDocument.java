package x10.trainup.product.infra.datascoures.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlavorVariantDocument {

    private String id;               // ID riêng cho hương
    private String flavor;           // Hương vị
    private int quantityInStock;     // Tồn kho
    private int quantitySold;        // Đã bán
    private boolean active;          // Trạng thái bán
}
