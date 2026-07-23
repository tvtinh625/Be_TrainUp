package x10.trainup.cart.core.usecases.RemoveListItemsUC;



import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemoveListItemsReq {

    @NotNull(message = "Danh sách items không được null")
    @NotEmpty(message = "Danh sách items không được rỗng")
    @Valid
    private List<CartItemIdentifier> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartItemIdentifier {

        @NotNull(message = "productId không được để trống")
        private String productId;

        @NotNull(message = "sizeId không được để trống")
        private String sizeId;

        @NotNull(message = "flavorId không được để trống")
        private String flavorId;
    }
}
