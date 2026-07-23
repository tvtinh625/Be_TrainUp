package x10.trainup.user.core.repositories;

import x10.trainup.commons.domain.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    UserEntity save(UserEntity user);

    Optional<UserEntity> findById(String id);

    List<UserEntity> findAll();

    void deleteById(String id);

    boolean existsByEmail(String email);

    Optional<UserEntity>findByEmail(String email);

    boolean existsById(String userId);

}
