package x10.trainup.shop.core.usecases.createShop;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import x10.trainup.commons.domain.entities.AddressEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateShopReq {

    @NotBlank(message = "Shop name must not be blank")
    @Size(max = 150, message = "Shop name must be at most 150 characters")
    private String name;

    @Valid
    private AddressShopReq address;
}
