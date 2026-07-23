package x10.trainup.commons.util;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component // Đảm bảo Spring tìm thấy và tạo Bean
public class MediaUrlResolverImpl implements IMediaUrlResolver {

    // 💡 Inject các giá trị cấu hình AWS từ application.yml
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName; // Ví dụ: kelly-demo-it

    @Value("${cloud.aws.region.static}")
    private String region; // Ví dụ: us-east-1

    // Tùy chọn: Nếu dùng CloudFront, bạn sẽ inject domain CDN ở đây.

    @Override
    public String resolvePublicUrl(String s3Key) {
        // Sử dụng định dạng S3 Virtual Host Style đã xác nhận:
        // https://[bucket].s3.[region].amazonaws.com/[key]
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                region,
                s3Key);
    }
}