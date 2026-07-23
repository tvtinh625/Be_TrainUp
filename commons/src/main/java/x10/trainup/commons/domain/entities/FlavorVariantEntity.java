package x10.trainup.commons.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlavorVariantEntity {

    private String id;               // ID riêng cho hương
    private String flavor;           // Hương vị (vd: Socola, Vani)
    private int quantityInStock;     // Số lượng tồn kho
    private int quantitySold;        // Số lượng đã bán
    private boolean active;          // Trạng thái: còn bán hay đã ngưng (true/false)
}
