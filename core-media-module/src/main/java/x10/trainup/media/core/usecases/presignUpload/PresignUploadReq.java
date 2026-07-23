package x10.trainup.media.core.usecases.presignUpload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.Map;



@Data
@Builder
@AllArgsConstructor
public class PresignUploadReq {
    String folder;
    String originalFilename; // để gợi ý ext
    String mediaType;        // image/video/file -> để ràng buộc contentType
    long   maxSizeBytes;     // hạn mức phía client
    Duration expiry;         // mặc định 10-15 phút
    Map<String, String> metadata;

}
