package x10.trainup.commons.util;

public interface IMediaPathResolver {

    // Phương thức để tạo S3 Key hoàn chỉnh (Ví dụ: media/user/a1b2c3d4.png)
    String resolveKey(String mediaType, String originalFileName);

    // Có thể thêm resolvePrefix nếu cần logic đơn giản hơn
    String resolvePrefix(String mediaType);
}