package x10.trainup.api.cms.controller.revenueController;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.api.cms.controller.revenueController.dto.RevenueByDateItem;
import x10.trainup.api.cms.controller.revenueController.dto.RevenueSummaryRes;
import x10.trainup.commons.domain.entities.OrderEntity;
import x10.trainup.commons.domain.enums.OrderStatus;
import x10.trainup.order.core.usecases.ICoreOrderService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements IRevenueService {

    private final ICoreOrderService orderService;

    @Override
    public RevenueSummaryRes getRevenueSummary(Instant from, Instant to, String groupBy) {
        List<OrderEntity> allOrders = orderService.getAllOrdersSortedByDateDesc();

        // Lọc đơn hàng trong khoảng thời gian
        List<OrderEntity> filteredOrders = allOrders.stream()
                .filter(order -> {
                    boolean afterFrom = from == null || !order.getCreatedAt().isBefore(from);
                    boolean beforeTo = to == null || !order.getCreatedAt().isAfter(to);
                    return afterFrom && beforeTo;
                })
                .collect(Collectors.toList());

        long totalOrders = filteredOrders.size();
        long completedOrders = filteredOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED || o.getStatus() == OrderStatus.CONFIRMED)
                .count();
        long cancelledOrders = filteredOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CANCELLED)
                .count();

        // Chỉ tính doanh thu của đơn hàng DELIVERED (hoặc CONFIRMED)
        List<OrderEntity> revenueOrders = filteredOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED || o.getStatus() == OrderStatus.CONFIRMED)
                .collect(Collectors.toList());

        BigDecimal totalRevenue = revenueOrders.stream()
                .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgOrderValue = BigDecimal.ZERO;
        if (completedOrders > 0) {
            avgOrderValue = totalRevenue.divide(BigDecimal.valueOf(completedOrders), 2, RoundingMode.HALF_UP);
        }

        // Thống kê theo trạng thái
        Map<String, BigDecimal> revenueByStatus = new HashMap<>();
        for (OrderEntity o : filteredOrders) {
            String status = o.getStatus().name();
            BigDecimal amount = o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO;
            revenueByStatus.put(status, revenueByStatus.getOrDefault(status, BigDecimal.ZERO).add(amount));
        }

        // Thống kê theo phương thức thanh toán
        Map<String, BigDecimal> revenueByPayment = new HashMap<>();
        for (OrderEntity o : revenueOrders) {
            String payment = o.getPaymentMethod() != null ? o.getPaymentMethod().name() : "UNKNOWN";
            BigDecimal amount = o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO;
            revenueByPayment.put(payment, revenueByPayment.getOrDefault(payment, BigDecimal.ZERO).add(amount));
        }

        // Thống kê theo thời gian (groupBy: day, week, month)
        DateTimeFormatter formatter;
        if ("month".equalsIgnoreCase(groupBy)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        } else if ("year".equalsIgnoreCase(groupBy)) {
            formatter = DateTimeFormatter.ofPattern("yyyy").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        } else {
            // Mặc định theo ngày
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        }

        Map<String, BigDecimal> dateMap = new TreeMap<>();
        for (OrderEntity o : revenueOrders) {
            String dateStr = formatter.format(o.getCreatedAt());
            BigDecimal amount = o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO;
            dateMap.put(dateStr, dateMap.getOrDefault(dateStr, BigDecimal.ZERO).add(amount));
        }

        List<RevenueByDateItem> revenueByDate = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : dateMap.entrySet()) {
            revenueByDate.add(new RevenueByDateItem(entry.getKey(), entry.getValue()));
        }

        return RevenueSummaryRes.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .avgOrderValue(avgOrderValue)
                .revenueByDate(revenueByDate)
                .revenueByStatus(revenueByStatus)
                .revenueByPayment(revenueByPayment)
                .build();
    }
}
