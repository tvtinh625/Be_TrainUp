package x10.trainup.post.core.updatePostUc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostReq {

    private String postId;              // ID bài viết cần cập nhật
    private String content;             // Nội dung mới
    private List<MultipartFile> newFiles; // File mới upload thêm
    private List<String> keepUrls;      // URL media muốn giữ lại (những file cũ không bị xoá)
}
