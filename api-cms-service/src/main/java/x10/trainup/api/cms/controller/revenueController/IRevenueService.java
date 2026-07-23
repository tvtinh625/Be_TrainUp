package x10.trainup.api.cms.controller.revenueController;

import x10.trainup.api.cms.controller.revenueController.dto.RevenueSummaryRes;
import java.time.Instant;

public interface IRevenueService {
    RevenueSummaryRes getRevenueSummary(Instant from, Instant to, String groupBy);
}
