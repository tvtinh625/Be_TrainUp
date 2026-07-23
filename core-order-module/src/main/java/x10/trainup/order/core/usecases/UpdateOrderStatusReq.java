package x10.trainup.order.core.usecases;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import x10.trainup.commons.domain.enums.OrderStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderStatusReq {
    
    @NotBlank(message = "Order ID is required")
    private String orderId;
    
    @NotNull(message = "Status is required")
    private OrderStatus status;
    
    private String note;  // Ghi chú khi cập nhật
}
