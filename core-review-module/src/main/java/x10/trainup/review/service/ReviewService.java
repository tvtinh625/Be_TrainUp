package x10.trainup.review.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import x10.trainup.review.domain.Review;
import x10.trainup.review.dto.ReviewDto;
import x10.trainup.review.dto.ReviewResponseDTO;

public interface ReviewService {

    // =========================
    // USER (Portal)
    // =========================


    /**
     * Tạo đánh giá sản phẩm
     */
    Review createReview(
            ReviewDto dto,
            String userId
    );


    /**
     * Lấy review đang hiển thị theo sản phẩm
     */
    Page<Review> findApprovedByProductId(
            String productId,
            Pageable pageable
    );


    /**
     * Lấy chi tiết review
     */
    Review findById(String id);


    /**
     * Cập nhật review
     */
    Review updateReview(
            String id,
            ReviewDto dto
    );


    /**
     * Xóa review
     */
    void deleteReview(String id);

    /**
     * Lấy danh sách productId đã được user review trong một đơn hàng cụ thể.
     * Dùng để FE biết sản phẩm nào đã review, hiển thị "Đã đánh giá".
     */
    java.util.List<String> getReviewedProductIdsByOrderId(String userId, String orderId);



    // =========================
    // ADMIN (CMS)
    // =========================


    /**
     * Lấy tất cả review
     */
    Page<Review> findAll(
            Pageable pageable
    );


    /**
     * Lọc review theo trạng thái
     * ACTIVE / HIDDEN
     */
    Page<Review> findByStatus(
            String status,
            Pageable pageable
    );


    /**
     * Ẩn review
     */
    Review hideReview(
            String id
    );
    /**
     * Hiển thị lại review
     */
    Review showReview(
            String id
    );
}