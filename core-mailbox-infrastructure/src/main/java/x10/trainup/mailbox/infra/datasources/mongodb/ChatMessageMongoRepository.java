package x10.trainup.mailbox.infra.datasources.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageMongoRepository extends MongoRepository<ChatMessageDocument, String> {

    List<ChatMessageDocument> findByConversationIdOrderByCreatedAtAsc(String conversationId);
}
