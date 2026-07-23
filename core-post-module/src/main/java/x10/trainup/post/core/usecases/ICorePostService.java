package x10.trainup.post.core.usecases;

import x10.trainup.commons.domain.entities.PostEntity;
import x10.trainup.post.core.updatePostUc.UpdatePostReq;
import x10.trainup.post.core.usecases.creatPostUc.CreatePostReq;

import java.util.List;

public interface ICorePostService {

    PostEntity create(CreatePostReq req, String authorId, String authorName);

    PostEntity update(UpdatePostReq req, String authorId);

    void delete(String postId, String authorId);

    List<PostEntity> getAll();

}
