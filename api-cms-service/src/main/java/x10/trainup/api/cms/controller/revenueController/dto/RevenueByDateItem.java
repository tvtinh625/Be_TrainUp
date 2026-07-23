package x10.trainup.api.cms.controller.revenueController.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueByDateItem {
    private String date;
    private BigDecimal revenue;
}
