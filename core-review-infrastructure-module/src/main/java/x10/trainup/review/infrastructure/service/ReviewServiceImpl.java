package x10.trainup.review.infrastructure.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import x10.trainup.review.domain.Review;
import x10.trainup.review.dto.ReviewDto;
import x10.trainup.review.infrastructure.entity.ReviewEntity;
import x10.trainup.review.infrastructure.mapper.ReviewMapper;
import x10.trainup.review.repository.ReviewRepository;
import x10.trainup.review.service.ReviewService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;


    // ==========================
    // USER (PORTAL)
    // ==========================

    @Override
    public Review createReview(ReviewDto dto, String userId) {

        ReviewEntity entity = ReviewEntity.builder()
                .id(UUID.randomUUID().toString())
                .productId(dto.getProductId())
                .orderId(dto.getOrderId())
                .userId(userId)
                .rating(dto.getRating())
                .title(dto.getTitle())
                .content(dto.getContent())
                .photos(dto.getPhotos())
                .status(Review.ReviewStatus.ACTIVE.name())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        ReviewEntity saved = reviewRepository.save(entity);

        return reviewMapper.toDomain(saved);
    }

    @Override
    public Page<Review> findApprovedByProductId(
            String productId,
            Pageable pageable
    ) {

        Page<ReviewEntity> entities =
                reviewRepository.findByProductIdAndStatus(
                        productId,
                        Review.ReviewStatus.ACTIVE.name(),
                        pageable
                );

        return entities.map(reviewMapper::toDomain);
    }

    @Override
    public Review findById(String id) {

        return reviewRepository.findById(id)
                .map(reviewMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Review updateReview(String id, ReviewDto dto) {

        ReviewEntity entity = reviewRepository.findById(id)
                .orElse(null);

        if (entity == null) {
            return null;
        }

        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setRating(dto.getRating());
        entity.setPhotos(dto.getPhotos());
        entity.setUpdatedAt(Instant.now());

        ReviewEntity updated = reviewRepository.save(entity);

        return reviewMapper.toDomain(updated);
    }

    @Override
    public void deleteReview(String id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public List<String> getReviewedProductIdsByOrderId(String userId, String orderId) {
        return reviewRepository.findByUserIdAndOrderId(userId, orderId)
                .stream()
                .map(ReviewEntity::getProductId)
                .collect(Collectors.toList());
    }

    // ==========================
    // ADMIN (CMS)
    // ==========================

    @Override
    public Page<Review> findAll(Pageable pageable) {

        Page<ReviewEntity> entities = reviewRepository.findAll(pageable);

        return entities.map(reviewMapper::toDomain);
    }

    @Override
    public Page<Review> findByStatus(String status, Pageable pageable) {

        Page<ReviewEntity> entities =
                reviewRepository.findByStatus(status, pageable);

        return entities.map(reviewMapper::toDomain);
    }

    @Override
    public Review hideReview(String id) {

        ReviewEntity entity = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        entity.setStatus(
                Review.ReviewStatus.HIDDEN.name()
        );

        entity.setUpdatedAt(Instant.now());

        ReviewEntity updated = reviewRepository.save(entity);

        return reviewMapper.toDomain(updated);
    }

    @Override
    public Review showReview(String id) {

        ReviewEntity entity = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        entity.setStatus(
                Review.ReviewStatus.ACTIVE.name()
        );

        entity.setUpdatedAt(Instant.now());

        ReviewEntity updated = reviewRepository.save(entity);

        return reviewMapper.toDomain(updated);
    }
}