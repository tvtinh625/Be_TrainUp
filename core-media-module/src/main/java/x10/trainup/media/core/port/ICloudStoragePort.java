package x10.trainup.media.core.port;

import x10.trainup.media.core.usecases.uploadMedia.UploadMediaReq;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaRes;

import java.io.IOException;

public interface ICloudStoragePort {
    UploadMediaRes generatePresignedUploadUrl(UploadMediaReq req, String s3Key);

    boolean deleteFile(String s3Key);
}