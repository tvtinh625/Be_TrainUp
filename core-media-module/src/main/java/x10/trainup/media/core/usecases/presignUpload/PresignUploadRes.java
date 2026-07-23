package x10.trainup.media.core.usecases.presignUpload;

import java.time.Duration;
import java.util.Map;

public class PresignUploadRes {
    String key;              // server chọn trước
    String url;              // PUT URL
    Map<String, String> headersRequired; // vd Content-Type
    Duration expiry;
}
