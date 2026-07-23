package x10.trainup.media.core.usecases;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.media.core.usecases.deleteMedia.DeleteMediaReq;
import x10.trainup.media.core.usecases.deleteMedia.IDeleteMediaUc;
import x10.trainup.media.core.usecases.uploadMedia.IUploadMediaUc;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaReq;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaRes;


@Service
@AllArgsConstructor
public class CoreMediaImpl implements ICoreMediaService{

    private final IUploadMediaUc uploadMediaUc;
    private final IDeleteMediaUc deleteMediaUc;

    @Override
    public UploadMediaRes generateUploadUrl(UploadMediaReq req) {
        return uploadMediaUc.uploadMedia(req);
    }

    @Override
    public boolean deleteMedia(DeleteMediaReq req) {
        return deleteMediaUc.deleteMedia(req);
    }




}