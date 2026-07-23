package x10.trainup.api.cms.controller.reviewController;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.review.domain.Review;
import x10.trainup.review.service.ReviewService;

@RestController
@RequestMapping("/api/admin/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    /**
     * GET ALL REVIEWS
     *
     * GET /api/admin/reviews
     * GET /api/admin/reviews?status=ACTIVE
     */
    @GetMapping
    public ResponseEntity<Page<Review>> getReviews(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);


        if(status != null && !status.isBlank()){

            return ResponseEntity.ok(
                    reviewService.findByStatus(
                            status.toUpperCase(),
                            pageable
                    )
            );
        }


        return ResponseEntity.ok(
                reviewService.findAll(pageable)
        );
    }



    /**
     * GET DETAIL
     *
     * GET /api/admin/reviews/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(
            @PathVariable String id
    ){

        Review review =
                reviewService.findById(id);


        if(review == null){

            return ResponseEntity.notFound()
                    .build();
        }


        return ResponseEntity.ok(review);
    }




    /**
     * HIDE REVIEW
     *
     * PUT /api/admin/reviews/{id}/hide
     */
    @PutMapping("/{id}/hide")
    public ApiResponse<Review> hideReview(
            @PathVariable String id
    ){

        return ApiResponse.of(
                200,
                "REVIEW.HIDE_SUCCESS",
                "Ẩn đánh giá thành công",
                reviewService.hideReview(id),
                "/api/admin/reviews/" + id + "/hide",
                null
        );
    }
    @PutMapping("/{id}/show")
    public ApiResponse<Review> showReview(
            @PathVariable String id
    ){

        return ApiResponse.of(
                200,
                "REVIEW.SHOW_SUCCESS",
                "Hiển thị đánh giá thành công",
                reviewService.showReview(id),
                "/api/admin/reviews/" + id + "/show",
                null
        );
    }




    /**
     * DELETE
     *
     * DELETE /api/admin/reviews/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable String id
    ){

        reviewService.deleteReview(id);

        return ResponseEntity.noContent()
                .build();
    }

}