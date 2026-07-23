package x10.trainup.post.core.repositories;

import x10.trainup.commons.domain.entities.PostEntity;

import java.util.List;

public interface IPostRepository {

    PostEntity save(PostEntity post);

    List<PostEntity> findAll();

    PostEntity findById(String id);

    void deleteById(String id);

    boolean existsById(String id);

    PostEntity update(PostEntity post);


}
