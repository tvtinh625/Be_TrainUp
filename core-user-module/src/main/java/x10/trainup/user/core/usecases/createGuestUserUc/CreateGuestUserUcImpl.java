package x10.trainup.user.core.usecases.createGuestUserUc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.entities.AddressEntity;
import x10.trainup.commons.domain.entities.RoleEntity;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.domain.enums.AuthProvider;
import x10.trainup.commons.domain.enums.Role;
import x10.trainup.commons.domain.enums.UserStatus;
import x10.trainup.user.core.repositories.IUserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CreateGuestUserUcImpl implements ICreateGuestUserUc {
    private final IUserRepository userRepository;
    @Override
    public UserEntity execute(CreateGuestUserReq req) {
        String normalizedPhone = req.getPhone().replaceAll("[^0-9]", "");
        String username = req.getRecipientName();
        String email;
        if (req.getEmail() == null || req.getEmail().trim().isEmpty()) {
            // Tạo email ảo dạng guest_<timestamp>@trainup.vn
            email = "guest_" + System.currentTimeMillis() + "@trainup.vn";
        } else {
            email = req.getEmail().trim();
        }
        AddressEntity address = AddressEntity.builder()
                .street(req.getStreet())
                .ward(req.getWard())
                .district(req.getDistrict())
                .province(req.getProvince())
                .country(req.getCountry() != null ? req.getCountry() : "Vietnam")
                .provinceId(req.getProvinceId())
                .districtId(req.getDistrictId())
                .wardCode(req.getWardCode())
                .build();
        List<RoleEntity> guestRoles = new ArrayList<>();
        RoleEntity guestRole = new RoleEntity(
                UUID.randomUUID().toString(),
                Role.GUEST,
                new HashSet<>()
        );
        guestRoles.add(guestRole);
        UserEntity guestUser = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .username(username)
                .email(email)
                .phone(req.getPhone())
                .password(null)
                .roles(guestRoles)
                .status(UserStatus.ACTIVE)
                .address(address)
                .emailVerified(false)
                .verifiedAt(null)
                .provider(AuthProvider.GUEST)
                .providerId(null)
                .avatarUrl(null)
                .coverUrl(null)
                .avatarPublicId(null)
                .coverPublicId(null)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return userRepository.save(guestUser);
    }
}