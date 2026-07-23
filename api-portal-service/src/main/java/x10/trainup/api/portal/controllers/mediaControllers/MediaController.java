package x10.trainup.api.portal.controllers.mediaControllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import x10.trainup.media.core.usecases.ICoreMediaService;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaReq;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaRes;


@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final ICoreMediaService coreMediaService; // Service đã implement UploadMediaImpl
    @PostMapping("/upload-url")
    public UploadMediaRes generateUploadUrl(@Valid @RequestBody UploadMediaReq req) {
        return coreMediaService.generateUploadUrl(req);
    }

}