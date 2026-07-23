package x10.trainup.shop.infra.datasoucre.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressShopDocument {
    private String street;
    private String ward;
    private String district;
    private String province;
    private String country;
    private int provinceId;
    private int districtId;
    private int wardCode;
}