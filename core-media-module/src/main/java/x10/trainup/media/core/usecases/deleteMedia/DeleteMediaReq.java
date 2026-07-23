package x10.trainup.media.core.usecases.deleteMedia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteMediaReq {


    @NotBlank(message = "S3 Key is required for deletion")
    @Size(max = 512, message = "S3 Key cannot exceed 512 characters")
    private String s3Key;
}