package x10.trainup.mailbox.infra.datasources.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
public class ChatMessageDocument {

    @Id
    private String id;

    @Field("conversation_id")
    private String conversationId;

    @Field("sender_id")
    private String senderId;

    @Field("sender_type")
    private String senderType; // "user", "admin"

    @Field("sender_name")
    private String senderName;

    @Field("sender_avatar")
    private String senderAvatar;

    @Field("text")
    private String text;

    @Field("created_at")
    private LocalDateTime createdAt;
}
