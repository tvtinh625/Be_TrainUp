package x10.trainup.api.portal.controllers.orderController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import x10.trainup.commons.domain.entities.OrderEntity;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.mailbox.core.ports.MailSenderPort;
import x10.trainup.order.core.usecases.ICoreOrderService;
import x10.trainup.order.core.usecases.createOrder.CreateOrderReq;
import x10.trainup.security.core.jwt.IJwtService;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    @Value("${app.frontend-url:https://fe-trainup.vercel.app}")
    private String frontendUrl;

    private final ICoreOrderService orderService;
    private final MailSenderPort mailSenderPort;
    private final IJwtService jwtService;
    private final HttpServletRequest request;

    private String path() {
        return request.getRequestURI();
    }

    private String traceId() {
        var id = (String) request.getAttribute("traceId");
        return (id != null) ? id : MDC.get("traceId");
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderEntity>> createOrder(
            @RequestBody @Valid CreateOrderReq req
    ) {
        OrderEntity order = orderService.createOrder(req);

        // Gửi email xác nhận đặt hàng (bất đồng bộ — không block response)
        sendOrderConfirmationEmail(order);

        ApiResponse<OrderEntity> response = ApiResponse.of(
                201,
                "ORDER.CREATED",
                "Đơn hàng đã được tạo thành công",
                order,
                path(),
                traceId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderEntity>>> getOrdersByUserId(
            @PathVariable String userId
    ) {
        List<OrderEntity> orders = orderService.getOrdersByUserId(userId);
        ApiResponse<List<OrderEntity>> response = ApiResponse.of(
                200,
                "ORDER.LIST_BY_USER_SUCCESS",
                "Lấy danh sách đơn hàng của người dùng thành công",
                orders,
                path(),
                traceId()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderEntity>> getOrderById(
            @PathVariable String orderId
    ) {
        return orderService.getOrderById(orderId)
                .map(order -> ResponseEntity.ok(ApiResponse.of(
                        200,
                        "ORDER.FOUND",
                        "Lấy chi tiết đơn hàng thành công",
                        order,
                        path(),
                        traceId()
                )))
                .orElse(ResponseEntity.status(404).body(ApiResponse.of(
                        404,
                        "ORDER.NOT_FOUND",
                        "Không tìm thấy đơn hàng",
                        null,
                        path(),
                        traceId()
                )));
    }

    // ==========================================
    // PRIVATE: Gửi email xác nhận đặt hàng
    // ==========================================

    private void sendOrderConfirmationEmail(OrderEntity order) {
        try {
            if (order.getShippingAddress() == null) return;
            String email = order.getShippingAddress().getEmail();
            if (email == null || email.isBlank() || email.contains("@trainup.vn")) return;

            String recipientName = order.getShippingAddress().getRecipientName();
            String orderNumber  = order.getOrderNumber() != null ? order.getOrderNumber() : order.getId();
            String totalAmount  = formatCurrency(order.getTotalAmount());
            String createdAt    = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));

            StringBuilder items = new StringBuilder();
            if (order.getItems() != null) {
                for (var item : order.getItems()) {
                    items.append(String.format(
                            "<tr><td style='padding:8px;border-bottom:1px solid #f0ebe3'>%s<br/><small style='color:#888'>%s • %s</small></td>"
                            + "<td style='padding:8px;text-align:center;border-bottom:1px solid #f0ebe3'>%d</td>"
                            + "<td style='padding:8px;text-align:right;border-bottom:1px solid #f0ebe3'>%s</td></tr>",
                            item.getProductName() != null ? item.getProductName() : "",
                            item.getSizeName()    != null ? item.getSizeName() : "",
                            item.getFlavorName()  != null ? item.getFlavorName() : "",
                            item.getQuantity(),
                            formatCurrency(item.getSubtotal())
                    ));
                }
            }

            String token = jwtService.generateGuestHistoryToken(email);
            String trackingLink = frontendUrl + "/guest/orders?token=" + token;

            String html = buildOrderConfirmationHtml(recipientName, orderNumber, totalAmount, createdAt, items.toString(), trackingLink);

            mailSenderPort.sendHtml(email, "🎉 TrainUp — Xác nhận đơn hàng " + orderNumber, html);
        } catch (Exception e) {
            // Không để lỗi email block luồng đặt hàng
            System.err.println("⚠️ Lỗi gửi email xác nhận đơn hàng: " + e.getMessage());
        }
    }

    private String formatCurrency(Object amount) {
        if (amount == null) return "0đ";
        try {
            double v = Double.parseDouble(amount.toString());
            return String.format("%,.0fđ", v);
        } catch (Exception e) {
            return amount + "đ";
        }
    }

    private String buildOrderConfirmationHtml(String name, String orderNumber,
                                               String total, String createdAt,
                                               String itemsHtml, String trackingLink) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body style='margin:0;padding:0;background:#f5f1eb;font-family:\"Segoe UI\",Arial,sans-serif'>"
                + "<div style='max-width:600px;margin:32px auto;background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,.08)'>"
                + "  <div style='background:linear-gradient(135deg,#1a1a2e 0%,#c68a2e 100%);padding:40px 32px;text-align:center'>"
                + "    <h1 style='color:#fff;margin:0;font-size:28px;letter-spacing:1px'>TrainUp Life</h1>"
                + "    <p style='color:#f5e9d0;margin:8px 0 0;font-size:14px'>Thực phẩm chức năng thể thao</p>"
                + "  </div>"
                + "  <div style='padding:32px'>"
                + "    <h2 style='color:#1a1a2e;margin-top:0'>🎉 Đặt hàng thành công!</h2>"
                + "    <p>Xin chào <strong>" + name + "</strong>,</p>"
                + "    <p>Cảm ơn bạn đã tin tưởng TrainUp Life! Đơn hàng của bạn đã được ghi nhận thành công.</p>"
                + "    <div style='background:#f9f6f2;border-radius:12px;padding:20px;margin:24px 0'>"
                + "      <div style='display:flex;justify-content:space-between;margin-bottom:8px'><span style='color:#888'>Mã đơn hàng</span><strong>" + orderNumber + "</strong></div>"
                + "      <div style='display:flex;justify-content:space-between;margin-bottom:8px'><span style='color:#888'>Thời gian</span><strong>" + createdAt + "</strong></div>"
                + "      <div style='display:flex;justify-content:space-between'><span style='color:#888'>Tổng thanh toán</span><strong style='color:#c68a2e;font-size:18px'>" + total + "</strong></div>"
                + "    </div>"
                + "    <div style='text-align:center;margin:32px 0'>"
                + "      <a href='" + trackingLink + "' style='display:inline-block;background:linear-gradient(135deg,#c68a2e,#e8a832);color:#fff;text-decoration:none;padding:14px 32px;border-radius:50px;font-size:15px;font-weight:700;letter-spacing:.5px;box-shadow:0 4px 12px rgba(198,138,46,.3)'>🚚 Theo dõi & Xem lịch sử đơn hàng</a>"
                + "    </div>"
                + "    <h3 style='color:#1a1a2e'>Sản phẩm đã đặt</h3>"
                + "    <table style='width:100%;border-collapse:collapse'>"
                + "      <thead><tr style='background:#f0ebe3'>"
                + "        <th style='padding:10px;text-align:left'>Sản phẩm</th>"
                + "        <th style='padding:10px;text-align:center'>SL</th>"
                + "        <th style='padding:10px;text-align:right'>Thành tiền</th>"
                + "      </tr></thead><tbody>" + itemsHtml + "</tbody></table>"
                + "    <p style='margin-top:24px;color:#666'>Chúng tôi sẽ liên hệ xác nhận và thông báo khi đơn hàng được giao. Nếu có thắc mắc, vui lòng liên hệ chúng tôi qua email hoặc fanpage.</p>"
                + "  </div>"
                + "  <div style='background:#1a1a2e;padding:20px;text-align:center'>"
                + "    <p style='color:#888;margin:0;font-size:12px'>© 2026 TrainUp Life — Sức mạnh từ dinh dưỡng đúng</p>"
                + "  </div>"
                + "</div></body></html>";
    }

    @GetMapping("/guest/history")
    public ResponseEntity<ApiResponse<List<OrderEntity>>> getGuestOrderHistory(
            @RequestParam String token
    ) {
        String email = jwtService.validateGuestHistoryToken(token);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.of(
                    401, "ORDER.TOKEN_INVALID", "Link theo dõi không hợp lệ hoặc đã hết hạn", null, path(), traceId()
            ));
        }

        List<OrderEntity> orders = orderService.getOrdersByGuestEmail(email);
        return ResponseEntity.ok(ApiResponse.of(
                200, "ORDER.GUEST_HISTORY_SUCCESS", "Lấy lịch sử mua hàng thành công", orders, path(), traceId()
        ));
    }

    @PostMapping("/guest/review-token")
    public ResponseEntity<ApiResponse<String>> generateGuestReviewTokenFromHistory(
            @RequestParam String token,
            @RequestParam String orderId
    ) {
        String email = jwtService.validateGuestHistoryToken(token);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.of(
                    401, "ORDER.TOKEN_INVALID", "Token lịch sử không hợp lệ hoặc đã hết hạn", null, path(), traceId()
            ));
        }

        Optional<OrderEntity> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.of(
                    404, "ORDER.NOT_FOUND", "Không tìm thấy đơn hàng", null, path(), traceId()
            ));
        }

        OrderEntity order = orderOpt.get();
        boolean isOwner = order.getShippingAddress() != null && email.equalsIgnoreCase(order.getShippingAddress().getEmail());
        if (!isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.of(
                    403, "ORDER.FORBIDDEN", "Bạn không sở hữu đơn hàng này", null, path(), traceId()
            ));
        }

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.of(
                    400, "ORDER.INVALID_STATUS", "Chỉ có thể đánh giá đơn hàng đã giao thành công", null, path(), traceId()
            ));
        }

        String reviewToken = jwtService.generateGuestReviewToken(orderId, email);
        return ResponseEntity.ok(ApiResponse.of(
                200, "ORDER.REVIEW_TOKEN_GENERATED", "Sinh token đánh giá thành công", reviewToken, path(), traceId()
        ));
    }
}