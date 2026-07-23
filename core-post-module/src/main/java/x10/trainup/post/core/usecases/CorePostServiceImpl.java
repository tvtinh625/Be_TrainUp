package x10.trainup.post.core.usecases;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.entities.PostEntity;
import x10.trainup.post.core.repositories.IPostRepository;
import x10.trainup.post.core.updatePostUc.IUpdatePostUc;
import x10.trainup.post.core.updatePostUc.UpdatePostReq;
import x10.trainup.post.core.usecases.creatPostUc.CreatePostReq;
import x10.trainup.post.core.usecases.creatPostUc.ICreatePostUc;
import x10.trainup.post.core.usecases.deletePostUc.IDeletePostUc;

import java.util.List;


@Service
@AllArgsConstructor
public class CorePostServiceImpl implements   ICorePostService{


    private final IDeletePostUc deletePostUc;
    private final ICreatePostUc createPostUc;
    private final IPostRepository iPostRepository;
    private final IUpdatePostUc updatePostUc;
    @Override
    public PostEntity create(CreatePostReq req, String authorId, String authorName) {
        return createPostUc.process(req, authorId, authorName);
    }

    @Override
    public List<PostEntity> getAll(){
        return  iPostRepository.findAll();
    }

    @Override
    public PostEntity update(UpdatePostReq req, String authorId) {
        return updatePostUc.process(req, authorId);
    }

    @Override
    public void delete(String postId, String authorId) {
        deletePostUc.delete( postId,  authorId);
    }
}
