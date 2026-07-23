package x10.trainup.media.core.usecases.uploadMedia;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadMediaRes {
    private String preSignedUploadUrl;
    private String mediaUrl;
    private String s3Key;
}
