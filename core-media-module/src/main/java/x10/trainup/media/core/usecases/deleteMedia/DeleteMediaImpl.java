package x10.trainup.media.core.usecases.deleteMedia;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import x10.trainup.media.core.port.ICloudStoragePort;
// Giả định có Interface Repository để xóa metadata DB
// import x10.trainup.media.core.port.IMediaRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteMediaImpl implements IDeleteMediaUc {

    private final ICloudStoragePort cloudStoragePort;
    // private final IMediaRepository mediaRepository; // Inject nếu có logic DB

    @Override
    public boolean deleteMedia(DeleteMediaReq req) {
        final String s3Key = req.getS3Key();
        log.info("Attempting to delete media with S3 Key: {}", s3Key);

        // 1. (Tùy chọn) Logic kiểm tra quyền truy cập hoặc kiểm tra trạng thái file trong DB
        // boolean isAuthorized = checkUserAuthorization(req.getUserId(), s3Key);
        // if (!isAuthorized) throw new AccessDeniedException("User not authorized to delete this file.");

        try {
            // 2. Gọi Port (Infrastructure) để xóa file khỏi Cloud Storage (S3)
            boolean s3Deleted = cloudStoragePort.deleteFile(s3Key);

            if (s3Deleted) {
                log.info("Successfully deleted file from S3: {}", s3Key);

                // 3. (Tùy chọn) Logic nghiệp vụ: Xóa metadata khỏi Database
                // mediaRepository.deleteByS3Key(s3Key);

                return true;
            } else {
                log.warn("File not found or deletion failed on S3 for key: {}", s3Key);
                // Vẫn có thể trả về true nếu file không tồn tại (Idempotence)
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting file {} from cloud storage.", s3Key, e);
            // Có thể rethrow exception nghiệp vụ nếu cần
            return false;
        }
    }
}