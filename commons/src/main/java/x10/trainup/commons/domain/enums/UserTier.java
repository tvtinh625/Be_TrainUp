package x10.trainup.commons.domain.enums;

public enum UserTier {
    BRONZE, // Chi tiêu < 5.000.000đ
    SILVER, // Chi tiêu >= 5.000.000đ, Giảm 2%
    GOLD, // Chi tiêu >= 10.000.000đ, Giảm 5%
    PLATINUM // Chi tiêu >= 20.000.000đ, Giảm 10%
}
