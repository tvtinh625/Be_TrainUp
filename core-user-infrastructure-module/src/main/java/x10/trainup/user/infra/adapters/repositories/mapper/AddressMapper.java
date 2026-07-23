package x10.trainup.user.infra.adapters.repositories.mapper;

import x10.trainup.commons.domain.entities.AddressEntity;
import x10.trainup.user.infra.datasources.mongo.AddressDocument;

public class AddressMapper {

    public static AddressDocument toDocument(AddressEntity entity) {
        if (entity == null) return null;

        return AddressDocument.builder()
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

    public static AddressEntity toEntity(AddressDocument doc) {
        if (doc == null) return null;

        return AddressEntity.builder()
                .street(doc.getStreet())
                .ward(doc.getWard())
                .district(doc.getDistrict())
                .province(doc.getProvince())
                .country(doc.getCountry())
                .provinceId(doc.getProvinceId())
                .districtId(doc.getDistrictId())
                .wardCode(doc.getWardCode())
                .build();
    }
}
