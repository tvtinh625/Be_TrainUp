package x10.trainup.api.portal.controllers.paymentController;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentInfo {
    private long providerOrderCode;
    private String qrCode;
    private String checkoutUrl;
}
