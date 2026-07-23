package x10.trainup.order.core.usecases.cancelOrder;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelOrderReq {

    @NotBlank(message = "Lý do hủy không được để trống")
    private String reason;
}