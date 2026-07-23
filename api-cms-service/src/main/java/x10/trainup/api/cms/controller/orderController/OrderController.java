package x10.trainup.api.cms.controller.orderController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import x10.trainup.commons.domain.entities.OrderEntity;
import x10.trainup.commons.domain.enums.OrderStatus;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.mailbox.core.ports.MailSenderPort;
import x10.trainup.order.core.usecases.ICoreOrderService;
import x10.trainup.order.core.usecases.UpdateOrderStatusReq;
import x10.trainup.security.core.jwt.IJwtService;
import x10.trainup.user.core.usecases.ICoreUserSerivce;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.domain.enums.AuthProvider;

import java.util.List;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final ICoreOrderService orderService;
    private final MailSenderPort mailSenderPort;
    private final IJwtService jwtService;
    private final ICoreUserSerivce userService;
    private final HttpServletRequest request;

    // URL FE — Guest sẽ truy cập vào đây để đánh giá
    private static final String FRONTEND_REVIEW_URL = "http://localhost:5173/review-guest";

    private String path() {
        return request.getRequestURI();
    }

    private String traceId() {
        var id = (String) request.getAttribute("traceId");
        if (id == null) id = "trace-" + System.currentTimeMillis();
        return id;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderEntity>>> getAllOrders() {
        List<OrderEntity> orders = orderService.getAllOrdersSortedByDateDesc();
        ApiResponse<List<OrderEntity>> response = ApiResponse.of(
                200,
                "ORDER.LIST_SUCCESS",
                "Lấy danh sách đơn hàng thành công",
                orders,
                path(),
                traceId()
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderEntity>> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderStatusReq req,
            HttpServletRequest request
    ) {
        System.out.println("========== CONTROLLER ==========");

        req.setOrderId(orderId);

        OrderEntity updated = orderService.updateOrderStatus(req);

        // Nếu chuyển sang CONFIRMED → gửi email cho Guest
        if (OrderStatus.CONFIRMED.equals(updated.getStatus())) {
            sendGuestReviewEmail(updated);
        }

        ApiResponse<OrderEntity> response = ApiResponse.of(
                HttpStatus.OK.value(),
                "ORDER.STATUS_UPDATED",
                "Cập nhật trạng thái đơn hàng thành công",
                updated,
                path(),
                traceId()
        );

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // PRIVATE: Gửi email link đánh giá cho Guest
    // =====================================================

    private void sendGuestReviewEmail(OrderEntity order) {
        try {
            System.out.println("🔍 [GuestReviewEmail] orderId=" + order.getId()
                    + " userId=" + order.getUserId()
                    + " shippingEmail=" + (order.getShippingAddress() != null ? order.getShippingAddress().getEmail() : "NO_ADDRESS"));

            // 1. Load user từ DB và kiểm tra AuthProvider.GUEST
            String userId = order.getUserId();
            if (userId == null) {
                System.out.println("⛔ [GuestReviewEmail] userId null → bỏ qua");
                return;
            }

            UserEntity user = userService.getUserById(userId).orElse(null);
            if (user == null) {
                System.out.println("⛔ [GuestReviewEmail] user không tìm thấy userId=" + userId);
                return;
            }

            System.out.println("👤 [GuestReviewEmail] user.provider=" + user.getProvider()
                    + " user.email=" + user.getEmail());

            // 2. Chỉ gửi cho Guest — User đăng nhập vào lịch sử đơn hàng để đánh giá
            boolean isGuest = AuthProvider.GUEST.equals(user.getProvider());
            if (!isGuest) {
                System.out.println("⛔ [GuestReviewEmail] không phải GUEST → bỏ qua");
                return;
            }

            // 3. Lấy email: ưu tiên shippingAddress.email (đơn mới),
            //    fallback sang user.email (đơn cũ đặt trước khi thêm field)
            String email = null;
            if (order.getShippingAddress() != null
                    && order.getShippingAddress().getEmail() != null
                    && !order.getShippingAddress().getEmail().isBlank()) {
                email = order.getShippingAddress().getEmail();
            } else {
                email = user.getEmail();
            }

            if (email == null || email.isBlank()) {
                System.out.println("⛔ [GuestReviewEmail] không tìm được email → bỏ qua");
                return;
            }

            System.out.println("📧 [GuestReviewEmail] Gửi email tới: " + email);

            // 4. Sinh token bảo mật (hết hạn sau 7 ngày)
            String reviewToken = jwtService.generateGuestReviewToken(order.getId(), email);

            String recipientName  = order.getShippingAddress() != null
                    ? order.getShippingAddress().getRecipientName()
                    : user.getUsername();
            String orderNumber    = order.getOrderNumber() != null ? order.getOrderNumber() : order.getId();
            String reviewLink     = FRONTEND_REVIEW_URL + "?token=" + reviewToken;

            StringBuilder products = new StringBuilder();
            if (order.getItems() != null) {
                for (var item : order.getItems()) {
                    products.append(String.format(
                            "<li style='padding:6px 0;border-bottom:1px solid #f0ebe3'>%s%s%s</li>",
                            item.getProductName() != null ? item.getProductName() : "",
                            item.getSizeName() != null ? " — " + item.getSizeName() : "",
                            item.getFlavorName() != null ? " / " + item.getFlavorName() : ""
                    ));
                }
            }

            String html = buildGuestReviewEmailHtml(recipientName, orderNumber, reviewLink, products.toString());

            mailSenderPort.sendHtml(
                    email,
                    "⭐ TrainUp — Hãy đánh giá đơn hàng " + orderNumber,
                    html
            );
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi gửi email review cho guest: " + e.getMessage());
        }
    }

    private String buildGuestReviewEmailHtml(String name, String orderNumber,
                                              String reviewLink, String productsHtml) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'></head>"
                + "<body style='margin:0;padding:0;background:#f5f1eb;font-family:\"Segoe UI\",Arial,sans-serif'>"
                + "<div style='max-width:600px;margin:32px auto;background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,.08)'>"
                + "  <div style='background:linear-gradient(135deg,#1a1a2e 0%,#c68a2e 100%);padding:40px 32px;text-align:center'>"
                + "    <h1 style='color:#fff;margin:0;font-size:28px;letter-spacing:1px'>TrainUp Life</h1>"
                + "    <p style='color:#f5e9d0;margin:8px 0 0;font-size:14px'>Thực phẩm chức năng thể thao</p>"
                + "  </div>"
                + "  <div style='padding:32px'>"
                + "    <h2 style='color:#1a1a2e;margin-top:0'>🎉 Đơn hàng đã được giao thành công!</h2>"
                + "    <p>Xin chào <strong>" + name + "</strong>,</p>"
                + "    <p>Đơn hàng <strong>#" + orderNumber + "</strong> của bạn đã được giao thành công. "
                + "Chúng tôi hy vọng bạn hài lòng với sản phẩm!</p>"
                + "    <h3 style='color:#1a1a2e'>Sản phẩm đã nhận:</h3>"
                + "    <ul style='padding-left:20px;margin:0 0 24px'>" + productsHtml + "</ul>"
                + "    <p>Ý kiến của bạn giúp chúng tôi cải thiện sản phẩm và phục vụ tốt hơn. "
                + "Nhấn vào nút bên dưới để chia sẻ đánh giá của bạn:</p>"
                + "    <div style='text-align:center;margin:32px 0'>"
                + "      <a href='" + reviewLink + "' "
                + "         style='display:inline-block;background:linear-gradient(135deg,#c68a2e,#e8a832);"
                + "                color:#fff;text-decoration:none;padding:16px 40px;border-radius:50px;"
                + "                font-size:16px;font-weight:700;letter-spacing:.5px;"
                + "                box-shadow:0 4px 16px rgba(198,138,46,.4)'>"
                + "        ⭐ Viết đánh giá ngay"
                + "      </a>"
                + "    </div>"
                + "    <p style='color:#999;font-size:12px;text-align:center'>"
                + "      Link có hiệu lực trong 7 ngày. Nếu không phải bạn, vui lòng bỏ qua email này."
                + "    </p>"
                + "  </div>"
                + "  <div style='background:#1a1a2e;padding:20px;text-align:center'>"
                + "    <p style='color:#888;margin:0;font-size:12px'>© 2026 TrainUp Life — Sức mạnh từ dinh dưỡng đúng</p>"
                + "  </div>"
                + "</div></body></html>";
    }
}
