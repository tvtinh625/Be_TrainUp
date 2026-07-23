package x10.trainup.mailbox.infra.datasources.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationMongoRepository extends MongoRepository<ConversationDocument, String> {

    Optional<ConversationDocument> findByUserId(String userId);

    List<ConversationDocument> findAllByOrderByUpdatedAtDesc();

    List<ConversationDocument> findByStatusOrderByUpdatedAtDesc(String status);
}
