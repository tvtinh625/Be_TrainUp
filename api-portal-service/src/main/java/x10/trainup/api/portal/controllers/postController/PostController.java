package x10.trainup.api.portal.controllers.postController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import x10.trainup.commons.domain.entities.PostEntity;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.post.core.updatePostUc.UpdatePostReq;
import x10.trainup.post.core.usecases.ICorePostService;
import x10.trainup.post.core.usecases.creatPostUc.CreatePostReq;
import x10.trainup.security.core.principal.UserPrincipal;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final ICorePostService postService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PostEntity>>> getAll(HttpServletRequest request) {
        List<PostEntity> posts = postService.getAll(); // lấy danh sách bài viết
        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "POST.LIST",
                        "Lấy danh sách bài viết thành công",
                        posts,
                        request.getRequestURI(),
                        "trace-" + System.currentTimeMillis()
                )
        );
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<PostEntity>> createPost(
            @ModelAttribute CreatePostReq req,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest request) {

        String authorId = user.getId();
        String authorName = user.getUsername();

        PostEntity post = postService.create(req, authorId, authorName);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "POST.CREATED",
                        "Tạo bài viết thành công",
                        post,
                        request.getRequestURI(),
                        "trace-" + System.currentTimeMillis()
                )
        );
    }

    @PutMapping(value = "/update", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<PostEntity>> updatePost(
            @ModelAttribute UpdatePostReq req,
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest request) {

        if (req.getPostId() == null || req.getPostId().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.of(
                            400,
                            "POST.UPDATE.INVALID_ID",
                            "Thiếu ID bài viết cần cập nhật",
                            null,
                            request.getRequestURI(),
                            "trace-" + System.currentTimeMillis()
                    )
            );
        }
        String authorId = user.getId();
        PostEntity updatedPost = postService.update(req, authorId);
        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "POST.UPDATED",
                        "Cập nhật bài viết thành công",
                        updatedPost,
                        request.getRequestURI(),
                        "trace-" + System.currentTimeMillis()
                )
        );
    }


}
