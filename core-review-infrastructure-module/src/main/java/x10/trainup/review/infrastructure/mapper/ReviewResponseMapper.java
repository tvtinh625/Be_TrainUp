package x10.trainup.review.infrastructure.mapper;

import org.springframework.stereotype.Component;
import x10.trainup.review.dto.ReviewResponseDTO;
import x10.trainup.review.infrastructure.entity.ReviewEntity;


@Component
public class ReviewResponseMapper {


    public ReviewResponseDTO toResponse(
            ReviewEntity entity
    ){

        if(entity == null)
            return null;


        return ReviewResponseDTO.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .orderId(entity.getOrderId())
                .userId(entity.getUserId())

                // sau này lấy từ User Service
                .username(entity.getUserId())

                .rating(entity.getRating())
                .title(entity.getTitle())
                .content(entity.getContent())
                .photos(entity.getPhotos())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}