package x10.trainup.user.core.usecases.updateStatusUserUc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.domain.enums.UserStatus;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.repositories.IUserRepository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class UpdateUserStatusImpl implements IUpdateUserStatusUc {

    private final IUserRepository iUserRepository;

    @Override
    @Transactional
    public UserEntity process(String userId, UpdateUserStatusReq req) {
        log.info("Processing status update for user: {} to: {}", userId, req.getStatus());

        // 1. Tìm user theo ID
        UserEntity user = iUserRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        UserError.USER_NOT_FOUND,
                        String.format("User not found with id: %s", userId),
                        Map.of("userId", userId)
                ));

        // 2. Lưu status cũ
        UserStatus oldStatus = user.getStatus();
        UserStatus newStatus = req.getStatus();

        // 3. Kiểm tra nếu status giống nhau
        if (oldStatus == newStatus) {
            log.warn("User {} is already in {} status", userId, oldStatus);
            throw new BusinessException(
                    UserError.SAME_STATUS,
                    String.format("User is already in %s status", oldStatus),
                    Map.of(
                            "userId", userId,
                            "currentStatus", oldStatus.name()
                    )
            );
        }

        // 4. Validate business rules
        validateStatusTransition(userId, oldStatus, newStatus);

        // 5. Cập nhật status
        try {
            user.setStatus(newStatus);
            UserEntity updatedUser = iUserRepository.save(user);

            log.info("User status updated successfully. UserId: {}, OldStatus: {}, NewStatus: {}, Reason: {}",
                    userId, oldStatus, newStatus, req.getReason());

            return updatedUser;

        } catch (Exception e) {
            log.error("Failed to update user status. UserId: {}, Error: {}", userId, e.getMessage());
            throw new BusinessException(
                    UserError.STATUS_UPDATE_FAILED,
                    "Failed to update user status",
                    Map.of(
                            "userId", userId,
                            "targetStatus", newStatus.name(),
                            "error", e.getMessage()
                    )
            );
        }
    }

    /**
     * Validate business rules cho việc chuyển đổi status
     */
    private void validateStatusTransition(String userId, UserStatus currentStatus, UserStatus newStatus) {
        Map<String, Object> details = new HashMap<>();
        details.put("userId", userId);
        details.put("currentStatus", currentStatus.name());
        details.put("targetStatus", newStatus.name());

        // Rule 1: BLOCKED không thể chuyển trực tiếp sang ACTIVE
        if (currentStatus == UserStatus.BLOCKED && newStatus == UserStatus.ACTIVE) {
            details.put("rule", "BLOCKED users must go through INACTIVE status first");
            throw new BusinessException(
                    UserError.INVALID_STATUS_TRANSITION,
                    "Cannot directly change BLOCKED user to ACTIVE. Must go through INACTIVE first.",
                    details
            );
        }

        // Rule 2: PENDING chỉ có thể chuyển sang ACTIVE hoặc BLOCKED
        if (currentStatus == UserStatus.PENDING &&
                newStatus != UserStatus.ACTIVE &&
                newStatus != UserStatus.BLOCKED) {
            details.put("rule", "PENDING users can only transition to ACTIVE or BLOCKED");
            details.put("allowedStatuses", new String[]{"ACTIVE", "BLOCKED"});
            throw new BusinessException(
                    UserError.INVALID_STATUS_TRANSITION,
                    "PENDING users can only be changed to ACTIVE or BLOCKED",
                    details
            );
        }

        // Rule 3: BLOCKED chỉ có thể chuyển sang INACTIVE
        if (currentStatus == UserStatus.BLOCKED &&
                newStatus != UserStatus.INACTIVE) {
            details.put("rule", "BLOCKED users can only transition to INACTIVE for review");
            details.put("allowedStatuses", new String[]{"INACTIVE"});
            throw new BusinessException(
                    UserError.INVALID_STATUS_TRANSITION,
                    "BLOCKED users can only be changed to INACTIVE for review",
                    details
            );
        }

        log.debug("Status transition validated: {} -> {}", currentStatus, newStatus);
    }
}