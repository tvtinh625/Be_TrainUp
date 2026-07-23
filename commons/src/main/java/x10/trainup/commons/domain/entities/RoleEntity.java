package x10.trainup.commons.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import x10.trainup.commons.domain.enums.Permission;
import x10.trainup.commons.domain.enums.Role;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleEntity {
    private String id;
    private Role name; // PT, CUSTOMER, ADMIN
    private Set<Permission> permissions; // quyền của role này

    // Kiểm tra nhanh role có permission hay không
    public boolean hasPermission(Permission permission) {
        return permissions != null && permissions.contains(permission);
    }
}
