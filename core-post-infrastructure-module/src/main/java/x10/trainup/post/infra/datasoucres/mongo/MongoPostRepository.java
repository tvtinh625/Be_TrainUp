package x10.trainup.post.infra.datasoucres.mongo;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoPostRepository extends MongoRepository<PostDocument,String> {


}
