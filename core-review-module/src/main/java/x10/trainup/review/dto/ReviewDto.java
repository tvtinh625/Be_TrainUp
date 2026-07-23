package x10.trainup.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private String productId;
    private String orderId;
    private Integer rating;
    private String title;
    private String content;
    private List<String> photos;
}
