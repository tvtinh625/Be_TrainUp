package x10.trainup.api.portal.controllers.reviewControllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import x10.trainup.commons.domain.entities.OrderEntity;
import x10.trainup.commons.domain.enums.OrderStatus;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.commons.exceptions.CommonError;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.order.core.usecases.ICoreOrderService;
import x10.trainup.review.domain.Review;
import x10.trainup.review.dto.ReviewDto;
import x10.trainup.review.service.ReviewService;
import x10.trainup.security.core.jwt.IJwtService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@Validated
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ICoreOrderService orderService;
    private final IJwtService jwtService;
    private final HttpServletRequest request;

    private String path() {
        return request.getRequestURI();
    }

    private String traceId() {
        var id = (String) request.getAttribute("traceId");
        return (id != null) ? id : MDC.get("traceId");
    }

    // =====================================================
    // 1. Tạo đánh giá — User đã đăng nhập (đơn CONFIRMED)
    // =====================================================

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody CreateReviewRequest req,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = null;
        if (principal instanceof org.springframework.security.core.Authentication auth) {
            if (auth.getPrincipal() instanceof x10.trainup.security.core.principal.UserPrincipal userPrincipal) {
                userId = userPrincipal.getId();
            }
        }
        if (userId == null) {
            userId = principal.getName();
        }

        // 1. Kiểm tra orderId bắt buộc
        if (req.getOrderId() == null || req.getOrderId().isBlank()) {
            throw new BusinessException(CommonError.REQUEST_INVALID,
                    "orderId là bắt buộc để viết đánh giá");
        }

        // 2. Tìm đơn hàng và kiểm tra đơn thuộc về user này
        OrderEntity order = orderService.getOrderById(req.getOrderId())
                .orElseThrow(() -> new BusinessException(CommonError.NOT_FOUND,
                        "Không tìm thấy đơn hàng"));

        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(CommonError.FORBIDDEN,
                    "Bạn không có quyền đánh giá đơn hàng này");
        }

        // 3. Đơn hàng phải ở trạng thái CONFIRMED
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BusinessException(CommonError.FORBIDDEN,
                    "Chỉ có thể đánh giá các đơn hàng đã giao thành công");
        }

        // 4. Sản phẩm phải nằm trong đơn hàng
        boolean productInOrder = order.getItems() != null && order.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(req.getProductId()));
        if (!productInOrder) {
            throw new BusinessException(CommonError.FORBIDDEN,
                    "Sản phẩm này không có trong đơn hàng của bạn");
        }

        // 5. Chưa đánh giá sản phẩm này trong đơn hàng này
        List<String> reviewedIds = reviewService.getReviewedProductIdsByOrderId(userId, req.getOrderId());
        if (reviewedIds.contains(req.getProductId())) {
            throw new BusinessException(CommonError.CONFLICT,
                    "Bạn đã đánh giá sản phẩm này rồi");
        }

        ReviewDto dto = buildDto(req.getProductId(), req.getOrderId(),
                req.getRating(), req.getTitle(), req.getComment(), req.getPhotos());

        Review created = reviewService.createReview(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(created));
    }

    // =====================================================
    // 2. Tạo đánh giá — Guest qua link email (JWT token)
    // =====================================================

    @PostMapping("/guest")
    public ResponseEntity<ApiResponse<ReviewResponse>> createGuestReview(
            @RequestParam("token") String token,
            @Valid @RequestBody GuestReviewRequest req
    ) {
        // Xác thực token và lấy orderId
        String orderId = jwtService.validateGuestReviewToken(token);
        if (orderId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.of(
                    401, "REVIEW.TOKEN_INVALID",
                    "Link đánh giá không hợp lệ hoặc đã hết hạn", null, path(), traceId()));
        }

        // Tìm đơn hàng
        OrderEntity order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new BusinessException(CommonError.NOT_FOUND,
                        "Không tìm thấy đơn hàng"));

        // Đơn hàng phải CONFIRMED
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BusinessException(CommonError.FORBIDDEN,
                    "Chỉ có thể đánh giá đơn hàng đã giao thành công");
        }

        // Sản phẩm phải trong đơn
        boolean productInOrder = order.getItems() != null && order.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(req.getProductId()));
        if (!productInOrder) {
            throw new BusinessException(CommonError.FORBIDDEN,
                    "Sản phẩm này không có trong đơn hàng");
        }

        // userId cho Guest = orderId (đã nhúng trong token, subject)
        String guestUserId = "guest_" + orderId;

        // Kiểm tra trùng đánh giá
        List<String> reviewed = reviewService.getReviewedProductIdsByOrderId(guestUserId, orderId);
        if (reviewed.contains(req.getProductId())) {
            throw new BusinessException(CommonError.CONFLICT,
                    "Sản phẩm này đã được đánh giá");
        }

        ReviewDto dto = buildDto(req.getProductId(), orderId,
                req.getRating(), req.getTitle(), req.getComment(), req.getPhotos());

        Review created = reviewService.createReview(dto, guestUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(
                201, "REVIEW.CREATED",
                "Đánh giá của bạn đã được ghi nhận, cảm ơn!",
                mapToResponse(created), path(), traceId()));
    }

    // =====================================================
    // 3. Lấy danh sách review của sản phẩm (public)
    // =====================================================

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @RequestParam("productId") String productId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewService.findApprovedByProductId(productId, pageable);
        return ResponseEntity.ok(reviewPage.map(this::mapToResponse));
    }

    // =====================================================
    // 4. Lấy productId đã review trong đơn (User đăng nhập)
    // =====================================================

    @GetMapping("/reviewed-items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<String>>> getReviewedItems(
            @RequestParam String orderId,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = null;
        if (principal instanceof org.springframework.security.core.Authentication auth) {
            if (auth.getPrincipal() instanceof x10.trainup.security.core.principal.UserPrincipal userPrincipal) {
                userId = userPrincipal.getId();
            }
        }
        if (userId == null) {
            userId = principal.getName();
        }
        List<String> reviewedProductIds = reviewService.getReviewedProductIdsByOrderId(userId, orderId);
        return ResponseEntity.ok(ApiResponse.of(
                200, "REVIEW.REVIEWED_ITEMS",
                "Lấy danh sách sản phẩm đã đánh giá thành công",
                reviewedProductIds, path(), traceId()));
    }

    // =====================================================
    // 5. Xác thực token Guest (FE dùng để load thông tin đơn hàng)
    // =====================================================

    @GetMapping("/guest/validate")
    public ResponseEntity<ApiResponse<OrderSummary>> validateGuestToken(
            @RequestParam("token") String token
    ) {
        String orderId = jwtService.validateGuestReviewToken(token);
        if (orderId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.of(
                    401, "REVIEW.TOKEN_INVALID",
                    "Link đánh giá không hợp lệ hoặc đã hết hạn", null, path(), traceId()));
        }

        OrderEntity order = orderService.getOrderById(orderId).orElse(null);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.of(
                    404, "REVIEW.ORDER_NOT_FOUND",
                    "Không tìm thấy đơn hàng", null, path(), traceId()));
        }

        String guestUserId = "guest_" + orderId;
        List<String> reviewed = reviewService.getReviewedProductIdsByOrderId(guestUserId, orderId);

        OrderSummary summary = new OrderSummary();
        summary.setOrderId(orderId);
        summary.setOrderNumber(order.getOrderNumber() != null ? order.getOrderNumber() : orderId);
        summary.setStatus(order.getStatus() != null ? order.getStatus().name() : "");
        summary.setReviewedProductIds(reviewed);
        summary.setItems(order.getItems() != null
                ? order.getItems().stream().map(item -> {
                    ItemSummary i = new ItemSummary();
                    i.setProductId(item.getProductId());
                    i.setProductName(item.getProductName());
                    i.setSizeName(item.getSizeName());
                    i.setFlavorName(item.getFlavorName());
                    i.setQuantity(item.getQuantity());
                    i.setImageUrl(item.getImageUrl());
                    return i;
                }).toList()
                : List.of());

        return ResponseEntity.ok(ApiResponse.of(
                200, "REVIEW.TOKEN_VALID",
                "Token hợp lệ", summary, path(), traceId()));
    }

    // ========== HELPERS ==========

    private ReviewDto buildDto(String productId, String orderId, Integer rating,
                                String title, String comment, List<String> photos) {
        ReviewDto dto = new ReviewDto();
        dto.setProductId(productId);
        dto.setOrderId(orderId);
        dto.setRating(rating);
        dto.setTitle(title);
        dto.setContent(comment);
        dto.setPhotos(photos);
        return dto;
    }

    private ReviewResponse mapToResponse(Review d) {
        ReviewResponse r = new ReviewResponse();
        r.setId(d.getId());
        r.setProductId(d.getProductId());
        r.setOrderId(d.getOrderId());
        r.setUserId(d.getUserId());
        r.setRating(d.getRating());
        r.setTitle(d.getTitle());
        r.setComment(d.getContent());
        r.setPhotos(d.getPhotos());
        r.setStatus(d.getStatus() != null ? d.getStatus().name() : null);
        r.setCreatedAt(d.getCreatedAt());
        r.setUpdatedAt(d.getUpdatedAt());
        return r;
    }

    // ========== DTOs ==========

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CreateReviewRequest {
        @NotBlank private String productId;
        @NotBlank(message = "orderId là bắt buộc") private String orderId;
        @NotNull @Min(1) @Max(5) private Integer rating;
        private String title;
        private String comment;
        private List<String> photos;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class GuestReviewRequest {
        @NotBlank private String productId;
        @NotNull @Min(1) @Max(5) private Integer rating;
        private String title;
        private String comment;
        private List<String> photos;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ReviewResponse {
        private String id;
        private String productId;
        private String orderId;
        private String userId;
        private Integer rating;
        private String title;
        private String comment;
        private List<String> photos;
        private String status;
        private java.time.Instant createdAt;
        private java.time.Instant updatedAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class OrderSummary {
        private String orderId;
        private String orderNumber;
        private String status;
        private List<String> reviewedProductIds;
        private List<ItemSummary> items;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ItemSummary {
        private String productId;
        private String productName;
        private String sizeName;
        private String flavorName;
        private Integer quantity;
        private String imageUrl;
    }
}
