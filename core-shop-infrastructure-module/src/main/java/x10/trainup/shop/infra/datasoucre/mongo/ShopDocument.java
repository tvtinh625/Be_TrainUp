package x10.trainup.shop.infra.datasoucre.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "shops")
public class ShopDocument {

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("address")
    private AddressShopDocument address;
}