package x10.trainup.loyalty.core.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.domain.enums.UserTier;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class LoyaltyServiceImpl implements ILoyaltyService {

    // Để cập nhật UserEntity
    private final x10.trainup.user.core.repositories.IUserRepository userRepository;

    @Override
    public UserEntity addPointsAndCheckTier(String userId, BigDecimal orderTotalAmount) {
        if (userId == null) return null;
        
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null || x10.trainup.commons.domain.enums.AuthProvider.GUEST.equals(user.getProvider())) {
            return null; // Guest không có loyalty
        }

        // 1. Cộng điểm: 1.000 VNĐ = 1 điểm, tức là 1 điểm = 1 VNĐ
        // Yêu cầu của người dùng: "điểm tích lũy đổi ra sẽ tương ứng với số tiền luôn VD: 10.000đ sẽ là 10.000VNĐ giảm"
        // Vậy nếu họ chi 100.000đ -> nên thưởng bao nhiêu? Giả sử thưởng 1% -> 1.000 điểm = 1.000 VNĐ
        BigDecimal earnedPoints = orderTotalAmount.multiply(BigDecimal.valueOf(0.01)); // 1%
        long pointsToAdd = earnedPoints.longValue();

        long currentPoints = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0L;
        user.setLoyaltyPoints(currentPoints + pointsToAdd);

        // 2. Cộng tổng chi tiêu
        BigDecimal currentSpent = user.getTotalSpent() != null ? user.getTotalSpent() : BigDecimal.ZERO;
        BigDecimal newTotalSpent = currentSpent.add(orderTotalAmount);
        user.setTotalSpent(newTotalSpent);

        // 3. Tính toán lại hạng (Tier)
        UserTier currentTier = user.getTier() != null ? user.getTier() : UserTier.BRONZE;
        UserTier newTier = calculateTier(newTotalSpent);
        
        // Chỉ cập nhật nếu thăng hạng (không hạ hạng)
        if (newTier.ordinal() > currentTier.ordinal()) {
            user.setTier(newTier);
        } else if (user.getTier() == null) {
            user.setTier(newTier);
        }

        return userRepository.save(user);
    }

    @Override
    public UserEntity redeemPoints(String userId, Long pointsToRedeem) {
        if (userId == null || pointsToRedeem == null || pointsToRedeem <= 0) return null;
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        long currentPoints = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0L;
        if (currentPoints < pointsToRedeem) {
            throw new RuntimeException("Không đủ điểm tích lũy");
        }
        
        user.setLoyaltyPoints(currentPoints - pointsToRedeem);
        return userRepository.save(user);
    }

    @Override
    public BigDecimal getTierDiscountPercentage(UserTier tier) {
        if (tier == null) return BigDecimal.ZERO;
        return switch (tier) {
            case SILVER -> BigDecimal.valueOf(2); // 2%
            case GOLD -> BigDecimal.valueOf(5); // 5%
            case PLATINUM -> BigDecimal.valueOf(10); // 10%
            default -> BigDecimal.ZERO;
        };
    }
    
    private UserTier calculateTier(BigDecimal totalSpent) {
        if (totalSpent.compareTo(BigDecimal.valueOf(20_000_000)) >= 0) {
            return UserTier.PLATINUM;
        } else if (totalSpent.compareTo(BigDecimal.valueOf(10_000_000)) >= 0) {
            return UserTier.GOLD;
        } else if (totalSpent.compareTo(BigDecimal.valueOf(5_000_000)) >= 0) {
            return UserTier.SILVER;
        }
        return UserTier.BRONZE;
    }
}
