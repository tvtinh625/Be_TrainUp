package x10.trainup.cart.core.usecases.createCartUc;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCartReq {
    @NotEmpty(message = "Giỏ hàng phải có ít nhất 1 sản phẩm")
    @Valid
    private List<CartItemReq> items;   // Danh sách sản phẩm trong giỏ
}
