package x10.trainup.user.infra.adapters.repositories;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import x10.trainup.user.core.repositories.IUserRepository;
import x10.trainup.user.infra.adapters.repositories.mapper.UserMapper;
import x10.trainup.user.infra.datasources.mongo.MongoUserRepository;
import x10.trainup.user.infra.datasources.mongo.UserDocument;
import x10.trainup.commons.domain.entities.UserEntity;

import java.util.List;
import java.util.Optional;


@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements IUserRepository {

    private final MongoUserRepository mongoUserRepository;

    @Override
    public UserEntity save(UserEntity user) {
        UserDocument saved = mongoUserRepository.save(UserMapper.toDocument(user));
        return UserMapper.toEntity(saved);
    }

    @Override
    public Optional<UserEntity> findById(String id) {
        return mongoUserRepository.findById(id).map(UserMapper::toEntity);
    }

    @Override
    public List<UserEntity> findAll() {
        return mongoUserRepository.findAll().stream()
                .map(UserMapper::toEntity)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        mongoUserRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return mongoUserRepository.existsByEmail(email);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return mongoUserRepository.findByEmail(email)
                .map(UserMapper::toEntity);
    }

    @Override
    public boolean existsById(String userId) {
        return mongoUserRepository.existsById(userId);
    }
}
