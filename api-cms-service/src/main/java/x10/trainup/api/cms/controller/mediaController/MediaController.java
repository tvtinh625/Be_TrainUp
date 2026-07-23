package x10.trainup.api.cms.controller.mediaController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import x10.trainup.media.core.usecases.ICoreMediaService;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaReq;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaRes;


@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final ICoreMediaService coreMediaService;
    @PostMapping("/upload-url")
    public UploadMediaRes generateUploadUrl(@Valid @RequestBody UploadMediaReq req) {
        return coreMediaService.generateUploadUrl(req);
    }
}
