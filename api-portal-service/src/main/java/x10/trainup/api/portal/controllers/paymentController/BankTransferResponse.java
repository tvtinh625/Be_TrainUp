package x10.trainup.api.portal.controllers.paymentController;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BankTransferResponse {
    private String qrCode;
    private PaymentInfo payment;
}
