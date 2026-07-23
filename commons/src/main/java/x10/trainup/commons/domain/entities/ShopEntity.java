package x10.trainup.commons.domain.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopEntity {
    private String id;
    private String name ;
    private AddressEntity addressEntity;
}
