package x10.trainup.post.infra.datasoucres.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class PostDocument {


    @Id
    private String id;                   // MongoDB _id
    private String authorId;             // ID người đăng
    private String authorName;           // Tên hiển thị người đăng
    private String content;              // Nội dung bài viết
    private List<String> mediaUrls;      // Ảnh / video
    private List<String> likeUserIds;    // Danh sách userId đã like
    private int likeCount;               // Số lượt like
    private List<CommentDocument> comments; // Danh sách comment (nếu bạn tách riêng thì để null)
    private int commentCount;            // Tổng comment
    private Instant createdAt;
    private Instant updatedAt;
}
