package x10.trainup.commons.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntity {
    private String id;
    private String postId;
    private String userId;
    private String userName;
    private String content;
    private Instant createdAt;
}
