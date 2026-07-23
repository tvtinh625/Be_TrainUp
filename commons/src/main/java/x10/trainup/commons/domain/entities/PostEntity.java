package x10.trainup.commons.domain.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class PostEntity {
    private String id;                 // ID bài viết
    private String authorId;           // ID người đăng (PT hoặc khách)
    private String authorName;         // Tên hiển thị người đăng
    private String content;            // Nội dung bài viết
    private List<String> mediaUrls;    // Danh sách ảnh/video đính kèm
    private List<String> likeUserIds;
    private int likeCount;
    private List<CommentEntity> comments;
    private int commentCount;
    private Instant createdAt;
    private Instant updatedAt;
}
