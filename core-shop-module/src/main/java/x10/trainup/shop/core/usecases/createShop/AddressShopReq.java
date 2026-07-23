package x10.trainup.shop.core.usecases.createShop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressShopReq {
    @NotBlank(message = "Street must not be blank")
    private String street;

    @NotBlank(message = "Ward must not be blank")
    private String ward;

    @NotBlank(message = "District must not be blank")
    private String district;

    @NotBlank(message = "Province must not be blank")
    private String province;

    @NotBlank(message = "Country must not be blank")
    private String country;

    @NotNull
    private int provinceId;
    @NotNull
    private int districtId;
    @NotNull
    private int wardCode;
}
