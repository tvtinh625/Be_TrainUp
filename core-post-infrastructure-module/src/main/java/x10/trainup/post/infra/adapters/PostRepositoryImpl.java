package x10.trainup.post.infra.adapters;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import x10.trainup.post.infra.adapters.mapper.PostMapper;
import x10.trainup.post.infra.datasoucres.mongo.MongoPostRepository;
import x10.trainup.post.core.repositories.IPostRepository;
import x10.trainup.commons.domain.entities.PostEntity;

import java.util.List;

@Repository
@AllArgsConstructor
public class PostRepositoryImpl implements IPostRepository {

    private final MongoPostRepository mongoPostRepository;

    @Override
    public PostEntity save(PostEntity post) {
        var doc = PostMapper.toDocument(post); // dùng static method
        var saved = mongoPostRepository.save(doc);
        return PostMapper.toEntity(saved);
    }


    @Override
    public List<PostEntity> findAll() {
        var docs = mongoPostRepository.findAll(); // Lấy toàn bộ document từ MongoDB
        // Chuyển từ List<PostDocument> → List<PostEntity>
        return docs.stream()
                .map(PostMapper::toEntity)
                .toList();
    }

    @Override
    public PostEntity findById(String id) {
        return mongoPostRepository.findById(id)
                .map(PostMapper::toEntity)
                .orElse(null);
    }

    @Override
    public void deleteById(String id) {
        mongoPostRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return mongoPostRepository.existsById(id);
    }

    @Override
    public PostEntity update(PostEntity post) {
        if (post.getId() == null || !mongoPostRepository.existsById(post.getId())) {
            throw new IllegalArgumentException("Post không tồn tại hoặc chưa có ID để update");
        }
        var doc = PostMapper.toDocument(post);
        var updated = mongoPostRepository.save(doc);
        return PostMapper.toEntity(updated);
    }
}
