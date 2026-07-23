package x10.trainup.shop.infra.adapters.mapper;

import x10.trainup.commons.domain.entities.AddressEntity;
import x10.trainup.commons.domain.entities.ShopEntity;
import x10.trainup.shop.infra.datasoucre.mongo.AddressShopDocument;
import x10.trainup.shop.infra.datasoucre.mongo.ShopDocument;

public class ShopMapper {

    // Convert ShopEntity to ShopDocument
    public static ShopDocument toDocument(ShopEntity entity) {
        if (entity == null) {
            return null;
        }

        return ShopDocument.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(toAddressDocument(entity.getAddressEntity()))
                .build();
    }

    // Convert ShopDocument to ShopEntity
    public static ShopEntity toEntity(ShopDocument document) {
        if (document == null) {
            return null;
        }

        return ShopEntity.builder()
                .id(document.getId())
                .name(document.getName())
                .addressEntity(toAddressEntity(document.getAddress()))
                .build();
    }

    // Convert AddressEntity to AddressShopDocument
    private static AddressShopDocument toAddressDocument(AddressEntity entity) {
        if (entity == null) {
            return null;
        }

        return AddressShopDocument.builder()
                .street(entity.getStreet())
                .ward(entity.getWard())
                .district(entity.getDistrict())
                .province(entity.getProvince())
                .country(entity.getCountry())
                .provinceId(entity.getProvinceId())
                .districtId(entity.getDistrictId())
                .wardCode(entity.getWardCode())
                .build();
    }

    // Convert AddressShopDocument to AddressEntity
    private static AddressEntity toAddressEntity(AddressShopDocument document) {
        if (document == null) {
            return null;
        }

        return AddressEntity.builder()
                .street(document.getStreet())
                .ward(document.getWard())
                .district(document.getDistrict())
                .province(document.getProvince())
                .country(document.getCountry())
                .provinceId(document.getProvinceId())
                .districtId(document.getDistrictId())
                .wardCode(document.getWardCode())
                .build();
    }
}