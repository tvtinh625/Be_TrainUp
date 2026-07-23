package x10.trainup.media.core.usecases.uploadMedia;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import x10.trainup.media.core.port.ICloudStoragePort;
import x10.trainup.commons.util.IMediaPathResolver; // Import từ module commons

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadMediaImpl implements IUploadMediaUc {

    private final ICloudStoragePort cloudStoragePort;
    private final IMediaPathResolver mediaPathResolver; // 💡 Đã inject Resolver

    @Override
    public UploadMediaRes uploadMedia(UploadMediaReq req) {
        log.info("Generating upload URL for media type: {} and file: {}",
                req.getMediaType(),
                req.getOriginalFileName());
        String s3Key = mediaPathResolver.resolveKey(
                req.getMediaType(),
                req.getOriginalFileName()
        );

        log.debug("Generated S3 Key: {}", s3Key);

        UploadMediaRes res = cloudStoragePort.generatePresignedUploadUrl(req, s3Key);
        return res;
    }

}