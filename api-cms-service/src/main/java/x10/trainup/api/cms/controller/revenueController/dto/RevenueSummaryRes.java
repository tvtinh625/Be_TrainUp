package x10.trainup.api.cms.controller.revenueController.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueSummaryRes {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long completedOrders;
    private long cancelledOrders;
    private BigDecimal avgOrderValue;
    private List<RevenueByDateItem> revenueByDate;
    private Map<String, BigDecimal> revenueByStatus;
    private Map<String, BigDecimal> revenueByPayment;
}
