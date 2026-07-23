package x10.trainup.post.core.updatePostUc;

import x10.trainup.commons.domain.entities.PostEntity;

public interface IUpdatePostUc {
    PostEntity process(UpdatePostReq req, String authorId);
}
