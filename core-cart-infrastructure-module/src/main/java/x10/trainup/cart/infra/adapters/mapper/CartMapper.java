package x10.trainup.cart.infra.adapters.mapper;

import x10.trainup.cart.infra.datasources.mongo.CartDocument;
import x10.trainup.cart.infra.datasources.mongo.CartItemDocument;
import x10.trainup.commons.domain.entities.CartEntity;
import x10.trainup.commons.domain.entities.CartItemEntity;

import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {

    // ================== CartEntity ↔ CartDocument ==================

    public static CartDocument toDocument(CartEntity entity) {
        if (entity == null) return null;

        return CartDocument.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .items(toDocumentList(entity.getItems()))
                .totalAmount(entity.getTotalAmount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static CartEntity toEntity(CartDocument document) {
        if (document == null) return null;

        return CartEntity.builder()
                .id(document.getId())
                .userId(document.getUserId())
                .items(toEntityList(document.getItems()))
                .totalAmount(document.getTotalAmount())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    // ================== CartItemEntity ↔ CartItemDocument ==================

    public static CartItemDocument toDocument(CartItemEntity entity) {
        if (entity == null) return null;

        return CartItemDocument.builder()
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .sizeId(entity.getSizeId())
                .sizeName(entity.getSizeName())
                .flavorId(entity.getFlavorId())
                .flavorName(entity.getFlavorName())
                .price(entity.getPrice())
                .quantity(entity.getQuantity())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    public static CartItemEntity toEntity(CartItemDocument document) {
        if (document == null) return null;

        return CartItemEntity.builder()
                .productId(document.getProductId())
                .productName(document.getProductName())
                .sizeId(document.getSizeId())
                .sizeName(document.getSizeName())
                .flavorId(document.getFlavorId())
                .flavorName(document.getFlavorName())
                .price(document.getPrice())
                .quantity(document.getQuantity())
                .imageUrl(document.getImageUrl())
                .build();
    }

    // ================== List converters ==================

    public static List<CartItemDocument> toDocumentList(List<CartItemEntity> entities) {
        return entities == null ? null : entities.stream()
                .map(CartMapper::toDocument)
                .collect(Collectors.toList());
    }

    public static List<CartItemEntity> toEntityList(List<CartItemDocument> documents) {
        return documents == null ? null : documents.stream()
                .map(CartMapper::toEntity)
                .collect(Collectors.toList());
    }
}
