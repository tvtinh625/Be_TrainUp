package x10.trainup.user.core.usecases.createUser;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.entities.RoleEntity;
import x10.trainup.commons.domain.enums.AuthProvider;
import x10.trainup.commons.domain.enums.Role;
import x10.trainup.commons.domain.enums.UserStatus;
import x10.trainup.user.core.errors.UserError;
import x10.trainup.user.core.repositories.IUserRepository;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.exceptions.BusinessException;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CreateUserImpl implements ICreateUserUc {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserEntity process(CreateUserReq input) {
        if (userRepository.existsByEmail(input.getEmail().trim().toLowerCase())) {
            throw new BusinessException(UserError.EMAIL_EXISTS, null);
        }
        String hashed = passwordEncoder.encode(input.getPassword());
        RoleEntity customerRole = new RoleEntity();
        customerRole.setId(UUID.randomUUID().toString());
        customerRole.setName(Role.CUSTOMER);
        customerRole.setPermissions(Set.of()); // chưa có quyền cụ thể thì để rỗng



        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .username(input.getUsername().trim())
                .password(hashed)
                .email(input.getEmail().trim().toLowerCase())
                .phone(input.getPhone() != null ? input.getPhone().trim() : null)
                .status(UserStatus.PENDING) // mặc định PENDING khi đăng ký
                .emailVerified(false)
                .roles(List.of(customerRole)) // 👈 gán role CUSTOMER mặc định
                .createdAt(Instant.now())     // 👈 dùng Instant thay vì LocalDateTime
                .updatedAt(Instant.now())
                .provider(AuthProvider.LOCAL)
                .providerId(null)
                .build();
        System.out.println("Before save user email = " + user.getEmail());
        UserEntity savedUser = userRepository.save(user);
        System.out.println("After save user email = " + savedUser.getEmail());
        return savedUser;
    }

}
