package x10.trainup.review.infrastructure.mapper;

import org.springframework.stereotype.Component;
import x10.trainup.review.domain.Review;
import x10.trainup.review.infrastructure.entity.ReviewEntity;

@Component
public class ReviewMapper {


    public Review toDomain(ReviewEntity entity) {

        if(entity == null)
            return null;


        return Review.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .orderId(entity.getOrderId())
                .userId(entity.getUserId())
                .rating(entity.getRating())
                .title(entity.getTitle())
                .content(entity.getContent())
                .photos(entity.getPhotos())
                .status(convertStatus(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }



    public ReviewEntity toEntity(Review domain) {

        if(domain == null)
            return null;


        return ReviewEntity.builder()
                .id(domain.getId())
                .productId(domain.getProductId())
                .orderId(domain.getOrderId())
                .userId(domain.getUserId())
                .rating(domain.getRating())
                .title(domain.getTitle())
                .content(domain.getContent())
                .photos(domain.getPhotos())
                .status(
                        domain.getStatus() != null
                                ? domain.getStatus().name()
                                : "ACTIVE"
                )
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }



    private Review.ReviewStatus convertStatus(String status){

        if(status == null)
            return Review.ReviewStatus.ACTIVE;


        return switch(status){

            case "ACTIVE",
                 "APPROVED",
                 "PENDING"
                    -> Review.ReviewStatus.ACTIVE;


            case "HIDDEN",
                 "REJECTED"
                    -> Review.ReviewStatus.HIDDEN;


            default
                    -> Review.ReviewStatus.ACTIVE;
        };
    }
}