    package x10.trainup.post.core.usecases.creatPostUc;

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
    public class CreatePostReq {

        private String content; // Nội dung bài viết

        // Danh sách file upload (ảnh, video,...)
        private List<MultipartFile> files;
    }
