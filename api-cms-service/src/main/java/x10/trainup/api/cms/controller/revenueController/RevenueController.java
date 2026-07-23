package x10.trainup.api.cms.controller.revenueController;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import x10.trainup.api.cms.controller.revenueController.dto.RevenueSummaryRes;
import x10.trainup.commons.response.ApiResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("api/revenue")
@RequiredArgsConstructor
public class RevenueController {

    private final IRevenueService revenueService;

    @GetMapping("/summary")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RevenueSummaryRes>> getRevenueSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "day") String groupBy
    ) {
        Instant fromInstant = null;
        Instant toInstant = null;

        if (from != null) {
            fromInstant = from.atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        }
        if (to != null) {
            toInstant = to.atTime(23, 59, 59).atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        }

        RevenueSummaryRes res = revenueService.getRevenueSummary(fromInstant, toInstant, groupBy);
        return ResponseEntity.ok(ApiResponse.of(200, "SUCCESS", "Thành công", res, null, null));
    }
}
