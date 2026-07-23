package x10.trainup.post.infra.datasoucres.mongo;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDocument {
    private String userId;
    private String username;
    private String content;
    private Instant createdAt;
}
