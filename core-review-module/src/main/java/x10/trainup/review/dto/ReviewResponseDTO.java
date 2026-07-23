package x10.trainup.review.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ReviewResponseDTO {

    private String id;

    private String productId;

    private String orderId;

    private String userId;

    private String username;

    private Integer rating;

    private String title;

    private String content;

    private List<String> photos;

    private Instant createdAt;
}