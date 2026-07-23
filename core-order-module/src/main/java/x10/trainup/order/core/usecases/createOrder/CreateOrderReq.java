package x10.trainup.order.core.usecases.createOrder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import x10.trainup.commons.domain.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderReq {

    private String userId;
    @NotNull(message = "Phương thức thanh toán là bắt buộc")
    private PaymentMethod paymentMethod;

    private String note;

    private String couponCode;
    private Long pointsToRedeem;

    @NotEmpty(message = "Đơn hàng phải có ít nhất 1 sản phẩm")
    @Valid
    private List<OrderItemReq> items;

    private BigDecimal shippingFee;
    @NotNull(message = "Địa chỉ giao hàng là bắt buộc")
    @Valid
    private ShippingAddressReq shippingAddress;
}
