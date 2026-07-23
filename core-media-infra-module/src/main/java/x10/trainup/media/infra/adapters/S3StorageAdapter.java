package x10.trainup.media.infra.adapters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import x10.trainup.media.core.port.ICloudStoragePort;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaReq;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaRes;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest; // 💡 ĐÃ SỬA IMPORT/CLASS NÀY

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3StorageAdapter implements ICloudStoragePort {

    private final S3Presigner   s3Presigner;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.presign-expiration-ms:300000}")
    private long expirationTimeMillis;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Override
    public UploadMediaRes generatePresignedUploadUrl(UploadMediaReq req, String s3Key) {

        Duration expirationTime = Duration.ofMillis(expirationTimeMillis);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(req.getContentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expirationTime)
                .putObjectRequest(putObjectRequest)
                .build();

        // 💡 DÙNG CLASS CHÍNH XÁC CHO KẾT QUẢ TRẢ VỀ: PresignedPutObjectRequest
        PresignedPutObjectRequest presignedUrl = s3Presigner.presignPutObject(presignRequest);

        // Tạo Media URL công khai
        String mediaUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                region,
                s3Key);

        return UploadMediaRes.builder()
                .preSignedUploadUrl(presignedUrl.url().toString())
                .mediaUrl(mediaUrl)
                .s3Key(s3Key)
                .build();
    }
    @Override
    public boolean deleteFile(String s3Key) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            DeleteObjectResponse response = s3Client.deleteObject(deleteRequest);


            if (response.deleteMarker() != null && response.deleteMarker()) {
                log.debug("Delete Marker was set for key: {}", s3Key);
            }
            return true;
        } catch (S3Exception e) {
            log.error("S3 error during deletion for key: {}", s3Key, e);
            return false;
        } catch (Exception e) {
            log.error("General error during deletion for key: {}", s3Key, e);
            return false;
        }
    }
}