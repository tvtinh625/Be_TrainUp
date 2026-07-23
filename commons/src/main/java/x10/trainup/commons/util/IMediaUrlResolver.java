// Đặt trong x10.trainup.commons.util.IMediaUrlResolver.java
package x10.trainup.commons.util;

/**
 * Resolver chịu trách nhiệm chuyển đổi S3 Key thành URL công khai hoàn chỉnh.
 * Điều này tách biệt logic hạ tầng (tên bucket, region, CDN) khỏi tầng Core.
 */
public interface IMediaUrlResolver {

    String resolvePublicUrl(String s3Key);
}