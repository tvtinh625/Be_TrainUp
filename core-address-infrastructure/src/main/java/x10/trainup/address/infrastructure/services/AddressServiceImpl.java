package x10.trainup.address.infrastructure.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import x10.trainup.address.core.dto.DistrictDTO;
import x10.trainup.address.core.dto.ProvinceDTO;
import x10.trainup.address.core.dto.ShippingFeeRequest;
import x10.trainup.address.core.dto.WardDTO;
import x10.trainup.address.core.usecases.IAddressService;

import java.util.List;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements IAddressService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String TOKEN = "c03a93bc-c2a1-11f0-a09b-aec1ea660f5d";

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", TOKEN);
        return headers;
    }

    // --- Các phương thức Master Data (getProvinces, getDistricts, getWards) giữ nguyên ---

    @Override
    public List<ProvinceDTO> getProvinces() {
        HttpEntity<String> entity = new HttpEntity<>(defaultHeaders());
        ResponseEntity<JsonNode> response =
                restTemplate.exchange(
                        "https://online-gateway.ghn.vn/shiip/public-api/master-data/province",
                        HttpMethod.GET,
                        entity,
                        JsonNode.class
                );
        return new ObjectMapper().convertValue(
                response.getBody().get("data"),
                new TypeReference<List<ProvinceDTO>>() {
                }
        );
    }

    @Override
    public List<DistrictDTO> getDistricts(int provinceId) {
        ObjectNode body = new ObjectMapper().createObjectNode();
        body.put("province_id", provinceId);
        HttpEntity<String> entity =
                new HttpEntity<>(body.toString(), defaultHeaders());
        ResponseEntity<JsonNode> response =
                restTemplate.exchange(
                        "https://online-gateway.ghn.vn/shiip/public-api/master-data/district",
                        HttpMethod.POST,
                        entity,
                        JsonNode.class
                );
        return new ObjectMapper().convertValue(
                response.getBody().get("data"),
                new TypeReference<List<DistrictDTO>>() {
                }
        );
    }

    @Override
    public List<WardDTO> getWards(int districtId) {
        ObjectNode body = new ObjectMapper().createObjectNode();
        body.put("district_id", districtId);
        HttpEntity<String> entity =
                new HttpEntity<>(body.toString(), defaultHeaders());
        ResponseEntity<JsonNode> response =
                restTemplate.exchange(
                        "https://online-gateway.ghn.vn/shiip/public-api/master-data/ward",
                        HttpMethod.POST,
                        entity,
                        JsonNode.class
                );
        return new ObjectMapper().convertValue(
                response.getBody().get("data"),
                new TypeReference<List<WardDTO>>() {
                }
        );
    }

    // --- Phương thức LẤY ID DỊCH VỤ HỢP LỆ (Giúp tránh lỗi tuyến đường) ---


    // --- Phương thức TÍNH PHÍ SHIP (Yêu cầu ShopID và ServiceID hợp lệ từ Controller) ---
    @Override
    public double calculateShippingFee(ShippingFeeRequest req) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode body = mapper.createObjectNode();

            // Thông tin shop - LẤY TỪ GHN DASHBOARD
            body.put("shop_id", 6117586);  // Shop ID của bạn từ ảnh

            // Địa chỉ gửi
            body.put("from_district_id", req.getFromDistrictId());
            body.put("from_ward_code", req.getFromWardCode());

            // Địa chỉ nhận
            body.put("to_district_id", req.getToDistrictId());
            body.put("to_ward_code", req.getToWardCode());

            // Thông tin gói hàng
            body.put("weight", req.getWeight());
            body.put("insurance_value", req.getInsuranceValue() != null ? req.getInsuranceValue() : 0);

            // Service Type ID (2 = Express, 5 = Economy)
            body.put("service_type_id", req.getServiceTypeId() != null ? req.getServiceTypeId() : 2);

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), defaultHeaders());

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee",
                    HttpMethod.POST,
                    entity,
                    JsonNode.class
            );

            JsonNode data = response.getBody().get("data");
            return data.get("total").asDouble();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tính phí ship: " + e.getMessage());
        }
    }
}