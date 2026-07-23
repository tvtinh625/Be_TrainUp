package x10.trainup.loyalty.core.usecases;

import x10.trainup.commons.domain.entities.UserEntity;
import java.math.BigDecimal;

public interface ILoyaltyService {
    /**
     * Thêm điểm và tính toán lại hạng khi đơn hàng hoàn thành
     */
    UserEntity addPointsAndCheckTier(String userId, BigDecimal orderTotalAmount);

    /**
     * Trừ điểm khi người dùng sử dụng điểm để giảm giá
     */
    UserEntity redeemPoints(String userId, Long pointsToRedeem);
    
    /**
     * Lấy mức giảm giá tương ứng với hạng của user
     * Ví dụ: GOLD -> giảm 5%
     */
    BigDecimal getTierDiscountPercentage(x10.trainup.commons.domain.enums.UserTier tier);
}
