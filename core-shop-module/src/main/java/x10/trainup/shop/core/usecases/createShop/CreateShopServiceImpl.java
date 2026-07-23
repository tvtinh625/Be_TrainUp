package x10.trainup.shop.core.usecases.createShop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.entities.AddressEntity;
import x10.trainup.commons.domain.entities.ShopEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.shop.core.errors.ShopErrors;
import x10.trainup.shop.core.repository.IRepositoryShop;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class CreateShopServiceImpl implements ICreateShopServiceUc {

    private final IRepositoryShop repositoryShop;

    @Override
    public ShopEntity process(CreateShopReq req) {

        // Validate shop name
        if (req.getName() == null || req.getName().trim().isEmpty()) {
            throw new BusinessException(ShopErrors.INVALID_SHOP_DATA, "Shop name is required");
        }

        // Kiểm tra tên shop đã tồn tại chưa
        if (repositoryShop.existsByName(req.getName())) {
            Map<String, Object> details = new HashMap<>();
            details.put("shopName", req.getName());
            throw new BusinessException(
                    ShopErrors.SHOP_NAME_ALREADY_EXISTS,
                    "Shop with name '" + req.getName() + "' already exists",
                    details
            );
        }
        if (req.getAddress() == null) {
            throw new BusinessException(ShopErrors.INVALID_ADDRESS_DATA, "Address is required");
        }

        try {
            // Map AddressShopReq to AddressEntity
            AddressEntity addressEntity = AddressEntity.builder()
                    .street(req.getAddress().getStreet())
                    .ward(req.getAddress().getWard())
                    .district(req.getAddress().getDistrict())
                    .province(req.getAddress().getProvince())
                    .country(req.getAddress().getCountry())
                    .provinceId(req.getAddress().getProvinceId())
                    .districtId(req.getAddress().getDistrictId())
                    .wardCode(req.getAddress().getWardCode())
                    .build();

            // Tạo ShopEntity
            ShopEntity shopEntity = ShopEntity.builder()
                    .name(req.getName().trim())
                    .addressEntity(addressEntity)
                    .build();

            // Lưu vào database
            ShopEntity savedShop = repositoryShop.save(shopEntity);

            log.info("Shop created successfully with ID: {}", savedShop.getId());

            return savedShop;

        } catch (NumberFormatException e) {
            Map<String, Object> details = new HashMap<>();
            details.put("wardCode", req.getAddress().getWardCode());
            throw new BusinessException(
                    ShopErrors.INVALID_ADDRESS_DATA,
                    "Ward code must be a valid number",
                    details
            );
        } catch (BusinessException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            log.error("Failed to create shop: {}", e.getMessage(), e);
            throw new BusinessException(
                    ShopErrors.SHOP_CREATION_FAILED,
                    "An error occurred while creating the shop: " + e.getMessage()
            );
        }
    }
}