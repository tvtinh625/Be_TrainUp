//package x10.trainup.user.infra.datasources.mongo;
//
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//@Repository
//public interface MongoUserRepository extends MongoRepository<UserDocument,String> {
//    boolean existsByEmail(String email);
//    Optional<UserDocument> findByEmail(String email);
//
//}
package x10.trainup.user.infra.datasources.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoUserRepository extends MongoRepository<UserDocument, String> {
    boolean existsByEmail(String email);
    Optional<UserDocument> findByEmail(String email);

}
