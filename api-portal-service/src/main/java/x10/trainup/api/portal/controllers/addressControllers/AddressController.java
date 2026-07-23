package x10.trainup.api.portal.controllers.addressControllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import x10.trainup.address.core.dto.*;
import x10.trainup.address.core.usecases.IAddressService;
import x10.trainup.commons.response.ApiResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/address")
@AllArgsConstructor
public class AddressController {

    private final IAddressService addressService;

    @GetMapping("/provinces")
    public ResponseEntity<ApiResponse<List<ProvinceDTO>>> getProvinces(HttpServletRequest request) {
        List<ProvinceDTO> provinces = addressService.getProvinces();

        ApiResponse<List<ProvinceDTO>> response = ApiResponse.of(
                HttpStatus.OK.value(),
                "ADDRESS.PROVINCE_SUCCESS",
                "Lấy danh sách tỉnh/thành phố thành công",
                provinces,
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/districts/{provinceId}")
    public ResponseEntity<ApiResponse<List<DistrictDTO>>> getDistricts(
            @PathVariable int provinceId,
            HttpServletRequest request) {

        List<DistrictDTO> districts = addressService.getDistricts(provinceId);

        ApiResponse<List<DistrictDTO>> response = ApiResponse.of(
                HttpStatus.OK.value(),
                "ADDRESS.DISTRICT_SUCCESS",
                "Lấy danh sách quận/huyện thành công",
                districts,
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/wards/{districtId}")
    public ResponseEntity<ApiResponse<List<WardDTO>>> getWards(
            @PathVariable int districtId,
            HttpServletRequest request) {

        List<WardDTO> wards = addressService.getWards(districtId);

        ApiResponse<List<WardDTO>> response = ApiResponse.of(
                HttpStatus.OK.value(),
                "ADDRESS.WARD_SUCCESS",
                "Lấy danh sách phường/xã thành công",
                wards,
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/shipping-fee")
    public ResponseEntity<ApiResponse<Double>> calculateShippingFee(
            @RequestBody ShippingFeeRequest shippingRequest,
            HttpServletRequest request) {

        double fee = addressService.calculateShippingFee(shippingRequest);

        ApiResponse<Double> response = ApiResponse.of(
                HttpStatus.OK.value(),
                "ADDRESS.SHIPPING_FEE_SUCCESS",
                "Tính phí vận chuyển thành công",
                fee,
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );

        return ResponseEntity.ok(response);
    }
}