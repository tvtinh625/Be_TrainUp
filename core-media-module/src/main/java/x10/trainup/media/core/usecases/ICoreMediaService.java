package x10.trainup.media.core.usecases;


import x10.trainup.media.core.usecases.deleteMedia.DeleteMediaReq;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaReq;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaRes;

public interface ICoreMediaService {

    UploadMediaRes generateUploadUrl(UploadMediaReq req);

    boolean deleteMedia(DeleteMediaReq req);
}