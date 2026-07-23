package x10.trainup.address.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProvinceDTO {
    // Trường đã sửa trước đó
    @JsonProperty("ProvinceID")
    private int provinceId;

    @JsonProperty("ProvinceName")
    private String provinceName;

    // <-- THÊM TRƯỜNG BỊ LỖI NÀY
    @JsonProperty("CountryID")
    private int countryId;

    // Nếu bạn cần Code, cũng thêm vào
    // @JsonProperty("Code")
    // private String code;
}