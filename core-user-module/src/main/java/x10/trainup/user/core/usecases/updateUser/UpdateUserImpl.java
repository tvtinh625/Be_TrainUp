package x10.trainup.user.core.usecases.updateUser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.media.core.usecases.ICoreMediaService;
import x10.trainup.media.core.usecases.deleteMedia.DeleteMediaReq;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.repositories.IUserRepository;
import x10.trainup.commons.util.IMediaUrlResolver; // 💡 Import Resolver Mới

import java.time.Instant;

@Slf4j
@Service
@AllArgsConstructor
public class UpdateUserImpl implements IUpdateUserUc {

    private final IUserRepository userRepository;
    private final ICoreMediaService coreMediaService;
    private final IMediaUrlResolver mediaUrlResolver; // 💡 Đã Inject Resolver

    @Override
    public UserEntity process(String userId, UpdateUserReq req) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessException(UserError.USER_NOT_FOUND, "Không tìm thấy người dùng."));

        handleAvatarUpdate(user, req);
        handleCoverUpdate(user, req);

        if (StringUtils.hasText(req.getUsername())) {
            user.setUsername(req.getUsername().trim());
        }
        if (StringUtils.hasText(req.getPhone())) {
            user.setPhone(req.getPhone().trim());
        }
        if (req.getAddress() != null) {
            user.setAddress(req.getAddress());
        }

        user.setUpdatedAt(Instant.now());
        UserEntity saved = userRepository.save(user);

        log.info("🎯 User {} updated successfully", userId);
        return saved;
    }

    private void handleAvatarUpdate(UserEntity user, UpdateUserReq req) {
        final String oldAvatarKey = user.getAvatarPublicId();

        if (StringUtils.hasText(req.getNewAvatarKey())) {
            if (StringUtils.hasText(oldAvatarKey)) {
                coreMediaService.deleteMedia(DeleteMediaReq.builder().s3Key(oldAvatarKey).build());
                log.info("🗑️ Old avatar deleted from S3: {}", oldAvatarKey);
            }

            user.setAvatarPublicId(req.getNewAvatarKey());
            // 💡 GỌI RESOLVER ĐÃ INJECT
            user.setAvatarUrl(mediaUrlResolver.resolvePublicUrl(req.getNewAvatarKey()));

        }
        else if (req.isRemoveAvatar() && StringUtils.hasText(oldAvatarKey)) {
            coreMediaService.deleteMedia(DeleteMediaReq.builder().s3Key(oldAvatarKey).build());
            user.setAvatarUrl(null);
            user.setAvatarPublicId(null);
            log.info("🗑️ Avatar removed from DB and S3 for user {}", user.getId());
        }
    }

    private void handleCoverUpdate(UserEntity user, UpdateUserReq req) {
        final String oldCoverKey = user.getCoverPublicId();
        if (StringUtils.hasText(req.getNewCoverKey())) {
            if (StringUtils.hasText(oldCoverKey)) {
                coreMediaService.deleteMedia(DeleteMediaReq.builder().s3Key(oldCoverKey).build());
                log.info("🗑️ Old cover deleted from S3: {}", oldCoverKey);
            }
            user.setCoverPublicId(req.getNewCoverKey());
            user.setCoverUrl(mediaUrlResolver.resolvePublicUrl(req.getNewCoverKey()));
        } else if (req.isRemoveCover() && StringUtils.hasText(oldCoverKey)) {
            coreMediaService.deleteMedia(DeleteMediaReq.builder().s3Key(oldCoverKey).build());
            user.setCoverUrl(null);
            user.setCoverPublicId(null);
            log.info("🗑️ Cover removed from DB and S3 for user {}", user.getId());
        }
    }

}