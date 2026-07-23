package x10.trainup.address.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShippingFeeResponse {
    @JsonProperty("total")
    private int total;

    @JsonProperty("service_fee")
    private int serviceFee;

    @JsonProperty("insurance_fee")
    private int insuranceFee;
}