package x10.trainup.post.infra.adapters.mapper;

import x10.trainup.post.infra.datasoucres.mongo.CommentDocument;
import x10.trainup.post.infra.datasoucres.mongo.PostDocument;
import x10.trainup.commons.domain.entities.CommentEntity;
import x10.trainup.commons.domain.entities.PostEntity;

import java.util.List;
import java.util.stream.Collectors;

public class PostMapper {

    public static PostDocument toDocument(PostEntity entity) {
        if (entity == null) return null;

        return PostDocument.builder()
                .id(entity.getId())
                .authorId(entity.getAuthorId())
                .authorName(entity.getAuthorName())
                .content(entity.getContent())
                .mediaUrls(entity.getMediaUrls())
                .likeUserIds(entity.getLikeUserIds())
                .likeCount(entity.getLikeCount())
                .commentCount(entity.getCommentCount())
                .comments(toCommentDocuments(entity.getComments()))
                .createdAt(entity.getUpdatedAt()) // hoặc bạn có thể tách riêng createdAt/updatedAt trong entity
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static PostEntity toEntity(PostDocument document) {
        if (document == null) return null;

        return PostEntity.builder()
                .id(document.getId())
                .authorId(document.getAuthorId())
                .authorName(document.getAuthorName())
                .content(document.getContent())
                .mediaUrls(document.getMediaUrls())
                .likeUserIds(document.getLikeUserIds())
                .likeCount(document.getLikeCount())
                .commentCount(document.getCommentCount())
                .comments(toCommentEntities(document.getComments()))
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    // --- Helper ---
    private static List<CommentDocument> toCommentDocuments(List<CommentEntity> comments) {
        if (comments == null) return null;
        return comments.stream()
                .map(c -> CommentDocument.builder()
                        .userId(c.getUserId())
                        .username(c.getUserName())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private static List<CommentEntity> toCommentEntities(List<CommentDocument> comments) {
        if (comments == null) return null;
        return comments.stream()
                .map(c -> CommentEntity.builder()
                        .userId(c.getUserId())
                        .userName(c.getUsername())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
