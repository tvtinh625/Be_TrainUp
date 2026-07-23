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
@Document(collection = "conversations")
public class ConversationDocument {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("user_name")
    private String userName;

    @Field("user_email")
    private String userEmail;

    @Field("user_avatar")
    private String userAvatar;

    @Field("status")
    private String status; // "pending", "active", "closed"

    @Field("last_message")
    private String lastMessage;

    @Field("last_sender")
    private String lastSender; // "user", "admin"

    @Field("assigned_admin_id")
    private String assignedAdminId;

    @Field("assigned_admin_name")
    private String assignedAdminName;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}
