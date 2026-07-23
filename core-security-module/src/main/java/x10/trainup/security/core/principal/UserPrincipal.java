package x10.trainup.security.core.principal;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.domain.entities.RoleEntity;
import x10.trainup.commons.domain.enums.Permission;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class UserPrincipal implements UserDetails {

    private final String id;
    private final String username;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean emailVerified;

    private UserPrincipal(String id, String username, String email,
                          String password, boolean emailVerified,
                          Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.emailVerified = emailVerified;
        this.authorities = authorities;
    }

    // Convert từ UserEntity -> UserPrincipal
    public static UserPrincipal from(UserEntity user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .flatMap(role -> {
                    // ROLE_ prefix cho role
                    GrantedAuthority roleAuth = (GrantedAuthority) () -> "ROLE_" + role.getName().name();

                    // Mỗi permission thành 1 authority
                    List<GrantedAuthority> permissionAuthorities =
                            Optional.ofNullable(role.getPermissions())
                                    .orElse(Set.of()) // nếu null thì dùng Set rỗng
                                    .stream()
                                    .map((Permission perm) -> (GrantedAuthority) perm::name)
                                    .toList();


                    return Stream.concat(Stream.of(roleAuth), permissionAuthorities.stream());
                })
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.isEmailVerified(),
                authorities
        );
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
