package x10.trainup.api.portal.controllers.paymentController;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBankTransferRequest {
    private Long orderId;
    private int amount;
    private String description;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
}
