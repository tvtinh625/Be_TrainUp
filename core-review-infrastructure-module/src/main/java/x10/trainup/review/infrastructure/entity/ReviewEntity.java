package x10.trainup.review.infrastructure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {
    @Id
    private String id;
    private String productId;
    private String orderId;
    private String userId;
    private Integer rating;
    private String title;
    private String content;
    private List<String> photos;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}
