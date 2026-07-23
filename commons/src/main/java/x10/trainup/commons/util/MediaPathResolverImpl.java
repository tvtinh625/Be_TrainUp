// Đặt trong module commons: x10.trainup.commons.util
package x10.trainup.commons.util;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component // Đảm bảo Spring tìm thấy và tạo Bean
public class MediaPathResolverImpl implements IMediaPathResolver {

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    @Override
    public String resolvePrefix(String mediaType) {
        return "media/" + mediaType.toLowerCase() + "/";
    }

    @Override
    public String resolveKey(String mediaType, String originalFileName) {
        String prefix = resolvePrefix(mediaType);
        String extension = getFileExtension(originalFileName);

        // Tạo Key: media/user/uuid.ext
        return prefix + UUID.randomUUID().toString() + extension;
    }
}