package x10.trainup.media.core.usecases.uploadMedia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadMediaReq {
    @NotBlank(message = "Original file name is required")
    @Size(max = 255, message = "Original file name must be less than 255 characters")
    private String originalFileName;


    @NotBlank(message = "Content type is required")
    private String contentType;

    @NotBlank(message = "Media type is required for path resolution")
    private String mediaType; // Ví dụ: "user", "blog", "produc
}