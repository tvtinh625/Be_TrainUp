package x10.trainup.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import x10.trainup.review.infrastructure.entity.ReviewEntity;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<ReviewEntity, String> {

    /**
     * Portal:
     * Lấy danh sách review đã được duyệt theo sản phẩm
     */
    Page<ReviewEntity> findByProductIdAndStatus(
            String productId,
            String status,
            Pageable pageable
    );

    /**
     * CMS:
     * Lấy danh sách review theo trạng thái
     */
    Page<ReviewEntity> findByStatus(
            String status,
            Pageable pageable
    );

    /**
     * CMS:
     * Lấy danh sách review theo sản phẩm
     */
    Page<ReviewEntity> findByProductId(
            String productId,
            Pageable pageable
    );

    /**
     * Kiểm tra user đã review sản phẩm này trong đơn hàng này chưa
     * (chặn đánh giá trùng lặp)
     */
    boolean existsByUserIdAndProductIdAndOrderId(String userId, String productId, String orderId);

    /**
     * Lấy danh sách productId đã được user này review trong đơn hàng
     * (để FE hiển thị "Đã đánh giá")
     */
    List<ReviewEntity> findByUserIdAndOrderId(String userId, String orderId);
}