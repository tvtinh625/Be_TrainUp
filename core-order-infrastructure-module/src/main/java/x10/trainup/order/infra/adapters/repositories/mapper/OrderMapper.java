package x10.trainup.order.infra.adapters.repositories.mapper;

import x10.trainup.commons.domain.entities.*;
import x10.trainup.order.infra.datasources.mongo.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderMapper {

    // ================================
    // ORDER ENTITY → DOCUMENT
    // ================================
    public static OrderDocument toDocument(OrderEntity entity) {
        if (entity == null) return null;

        return OrderDocument.builder()
                .id(entity.getId() != null ? entity.getId() : UUID.randomUUID().toString())
                .orderNumber(entity.getOrderNumber())
                .userId(entity.getUserId())
                .status(entity.getStatus())
                .paymentStatus(entity.getPaymentStatus())
                .paymentMethod(entity.getPaymentMethod())
                .items(toOrderItemDocuments(entity.getItems()))
                .subtotal(entity.getSubtotal())
                .shippingFee(entity.getShippingFee())
                .couponCode(entity.getCouponCode())
                .couponDiscount(entity.getCouponDiscount())
                .pointsRedeemed(entity.getPointsRedeemed())
                .pointsDiscount(entity.getPointsDiscount())
                .tierDiscount(entity.getTierDiscount())
                .pointsEarned(entity.getPointsEarned())
                .totalAmount(entity.getTotalAmount())
                .shippingAddress(toShippingAddressDocument(entity.getShippingAddress()))
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ================================
    // ORDER DOCUMENT → ENTITY
    // ================================
    public static OrderEntity toEntity(OrderDocument document) {
        if (document == null) return null;

        return OrderEntity.builder()
                .id(document.getId())
                .orderNumber(document.getOrderNumber())
                .userId(document.getUserId())
                .status(document.getStatus())
                .paymentStatus(document.getPaymentStatus())
                .paymentMethod(document.getPaymentMethod())
                .items(toOrderItemEntities(document.getItems()))
                .subtotal(document.getSubtotal())
                .shippingFee(document.getShippingFee())
                .couponCode(document.getCouponCode())
                .couponDiscount(document.getCouponDiscount())
                .pointsRedeemed(document.getPointsRedeemed())
                .pointsDiscount(document.getPointsDiscount())
                .tierDiscount(document.getTierDiscount())
                .pointsEarned(document.getPointsEarned())
                .totalAmount(document.getTotalAmount())
                .shippingAddress(toShippingAddressEntity(document.getShippingAddress()))
                .note(document.getNote())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    // ================================
    // SHIPPING ADDRESS MAPPING
    // ================================
    private static ShippingAddressDocument toShippingAddressDocument(ShippingAddressEntity entity) {
        if (entity == null) return null;

        return ShippingAddressDocument.builder()
                .recipientName(entity.getRecipientName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())

                .address(entity.getAddress())
                .ward(entity.getWard())
                .district(entity.getDistrict())
                .city(entity.getCity())
                .country(entity.getCountry())

                .provinceId(entity.getProvinceId())
                .districtId(entity.getDistrictId())
                .wardCode(entity.getWardCode())

                .fullAddress(entity.getFullAddress())
                .build();
    }

    private static ShippingAddressEntity toShippingAddressEntity(ShippingAddressDocument doc) {
        if (doc == null) return null;

        return ShippingAddressEntity.builder()
                .recipientName(doc.getRecipientName())
                .email(doc.getEmail())
                .phoneNumber(doc.getPhoneNumber())
                .address(doc.getAddress())
                .ward(doc.getWard())
                .district(doc.getDistrict())
                .city(doc.getCity())
                .country(doc.getCountry())
                .provinceId(doc.getProvinceId())
                .districtId(doc.getDistrictId())
                .wardCode(doc.getWardCode())
                .fullAddress(doc.getFullAddress())
                .build();
    }

    // ================================
    // ORDER ITEM MAPPER
    // ================================
    private static List<OrderItemDocument> toOrderItemDocuments(List<OrderItemEntity> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(OrderMapper::toOrderItemDocument)
                .collect(Collectors.toList());
    }

    private static List<OrderItemEntity> toOrderItemEntities(List<OrderItemDocument> documents) {
        if (documents == null) return null;
        return documents.stream()
                .map(OrderMapper::toOrderItemEntity)
                .collect(Collectors.toList());
    }

    private static OrderItemDocument toOrderItemDocument(OrderItemEntity entity) {
        if (entity == null) return null;

        return OrderItemDocument.builder()
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .sizeId(entity.getSizeId())
                .sizeName(entity.getSizeName())
                .flavorId(entity.getFlavorId())
                .flavorName(entity.getFlavorName())
                .price(entity.getPrice())
                .quantity(entity.getQuantity())
                .subtotal(entity.getSubtotal())
                .imageUrl(entity.getImageUrl())
                .build();
    }

    private static OrderItemEntity toOrderItemEntity(OrderItemDocument document) {
        if (document == null) return null;

        return OrderItemEntity.builder()
                .productId(document.getProductId())
                .productName(document.getProductName())
                .sizeId(document.getSizeId())
                .sizeName(document.getSizeName())
                .flavorId(document.getFlavorId())
                .flavorName(document.getFlavorName())
                .price(document.getPrice())
                .quantity(document.getQuantity())
                .subtotal(document.getSubtotal())
                .imageUrl(document.getImageUrl())
                .build();
    }
}
