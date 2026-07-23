package x10.trainup.user.core.utils;



import java.time.LocalDate;
import java.util.UUID;

public class S3FileUtil {

    private static final String BASE_URL = "https://kelly-demo-it.s3.us-east-1.amazonaws.com/"; // hoặc domain CDN bạn dùng

    public static String generateFileKey(String folder, String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String date = LocalDate.now().toString().replace("-", "");
        String uuid = UUID.randomUUID().toString();
        return folder + "/" + date + "/" + uuid + extension;
    }

    public static String buildPublicUrl(String key) {
        return BASE_URL + key;
    }
}
