package x10.trainup.post.core.usecases.creatPostUc;

import x10.trainup.commons.domain.entities.PostEntity;

public interface ICreatePostUc {

    PostEntity process(CreatePostReq req, String authorId, String authorName);
}
