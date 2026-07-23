package x10.trainup.api.portal.controllers.paymentController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import x10.trainup.api.portal.config.PayOSProperties;
import x10.trainup.commons.response.ApiResponse;

@RestController
@RequestMapping("api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PayOS payOS;
    private final PayOSProperties payOSProperties;
    private final HttpServletRequest request;

    private String path() {
        return request.getRequestURI();
    }

    private String traceId() {
        var id = (String) request.getAttribute("traceId");
        return (id != null) ? id : MDC.get("traceId");
    }

    /**
     * Tạo link thanh toán PayOS và trả về mã QR cho FE.
     * POST /api/payments/create-bank-transfer
     */
    @PostMapping("/create-bank-transfer")
    public ResponseEntity<ApiResponse<BankTransferResponse>> createBankTransfer(
            @RequestBody CreateBankTransferRequest req
    ) {
        try {
            // orderCode phải là số nguyên dương và duy nhất (timestamp giây)
            long orderCode = System.currentTimeMillis() / 1000L;

            // description PayOS: tối đa 25 ký tự, chỉ chữ, số và khoảng trắng
            String rawDesc = req.getDescription() != null ? req.getDescription() : "TrainUp Order";
            String description = rawDesc.replaceAll("[^a-zA-Z0-9 ]", "").trim();
            if (description.length() > 25) {
                description = description.substring(0, 25);
            }
            if (description.isEmpty()) {
                description = "TrainUp Order";
            }

            CreatePaymentLinkRequest paymentRequest = CreatePaymentLinkRequest.builder()
                    .orderCode(orderCode)
                    .amount((long) req.getAmount())
                    .description(description)
                    .returnUrl(payOSProperties.getReturnUrl())
                    .cancelUrl(payOSProperties.getCancelUrl())
                    .buyerName(req.getCustomerName())
                    .buyerEmail(req.getCustomerEmail())
                    .buyerPhone(req.getCustomerPhone())
                    .build();

            CreatePaymentLinkResponse paymentLinkResponse = payOS.paymentRequests().create(paymentRequest);

            String qrCode = paymentLinkResponse.getQrCode();

            PaymentInfo paymentInfo = PaymentInfo.builder()
                    .providerOrderCode(paymentLinkResponse.getOrderCode())
                    .qrCode(qrCode)
                    .checkoutUrl(paymentLinkResponse.getCheckoutUrl())
                    .build();

            BankTransferResponse responseData = BankTransferResponse.builder()
                    .qrCode(qrCode)
                    .payment(paymentInfo)
                    .build();

            log.info("Tạo mã QR thành công cho orderCode: {}", orderCode);

            return ResponseEntity.ok(ApiResponse.of(
                    200,
                    "PAYMENT.QR_CREATED",
                    "Tạo mã QR thanh toán thành công",
                    responseData,
                    path(),
                    traceId()
            ));

        } catch (Exception e) {
            log.error("Lỗi khi tạo thanh toán PayOS: {}", e.getMessage(), e);

            return ResponseEntity.status(500).body(ApiResponse.of(
                    500,
                    "PAYMENT.CREATE_FAILED",
                    "Không thể tạo mã QR: " + e.getMessage(),
                    null,
                    path(),
                    traceId()
            ));
        }
    }
}
