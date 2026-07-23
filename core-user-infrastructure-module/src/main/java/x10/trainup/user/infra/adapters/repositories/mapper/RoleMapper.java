package x10.trainup.user.infra.adapters.repositories.mapper;

import x10.trainup.commons.domain.entities.RoleEntity;
import x10.trainup.user.infra.datasources.mongo.RoleDocument;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoleMapper {

    public static RoleDocument toDocument(RoleEntity entity) {
        if (entity == null) return null;
        return new RoleDocument(
                entity.getId() != null ? entity.getId() : UUID.randomUUID().toString(),
                entity.getName(),
                entity.getPermissions() != null ? entity.getPermissions() : Set.of()
        );
    }

    public static RoleEntity toEntity(RoleDocument doc) {
        if (doc == null) return null;
        return new RoleEntity(
                doc.getId(),
                doc.getName(),
                doc.getPermissions()
        );
    }

    public static List<RoleDocument> toDocuments(List<RoleEntity> entities) {
        return entities == null ? List.of() : entities.stream()
                .map(RoleMapper::toDocument)
                .collect(Collectors.toList());
    }

    public static List<RoleEntity> toEntities(List<RoleDocument> docs) {
        return docs == null ? List.of() : docs.stream()
                .map(RoleMapper::toEntity)
                .collect(Collectors.toList());
    }
}
