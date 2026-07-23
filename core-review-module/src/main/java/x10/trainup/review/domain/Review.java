package x10.trainup.review.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private String id;
    private String productId;
    private String userId;
    private String orderId;       // ID đơn hàng — dùng để kiểm tra quyền đánh giá
    private Integer rating;
    private String title;
    private String content;
    private List<String> photos;
    private ReviewStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public enum ReviewStatus {
        ACTIVE,
        HIDDEN
    }
}
