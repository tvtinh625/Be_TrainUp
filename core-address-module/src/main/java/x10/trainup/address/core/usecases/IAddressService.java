package x10.trainup.address.core.usecases;


import x10.trainup.address.core.dto.DistrictDTO;
import x10.trainup.address.core.dto.ProvinceDTO;
import x10.trainup.address.core.dto.ShippingFeeRequest;
import x10.trainup.address.core.dto.WardDTO;

import java.util.List;

public interface IAddressService {
    List<ProvinceDTO> getProvinces();
    List<DistrictDTO> getDistricts(int provinceId);
    List<WardDTO> getWards(int districtId);

    double calculateShippingFee(ShippingFeeRequest request);
}
