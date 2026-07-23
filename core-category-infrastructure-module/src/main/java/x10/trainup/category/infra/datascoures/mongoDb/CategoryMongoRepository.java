package x10.trainup.category.infra.datascoures.mongoDb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryMongoRepository extends MongoRepository<CategoryDocument, String> {
    Optional<CategoryDocument> findByName(String name);

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<CategoryDocument> searchByNameContainsIgnoreCase(String name);
}
