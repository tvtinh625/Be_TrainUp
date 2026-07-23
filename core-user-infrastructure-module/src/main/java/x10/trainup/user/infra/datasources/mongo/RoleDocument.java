package x10.trainup.user.infra.datasources.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import x10.trainup.commons.domain.enums.Permission;
import x10.trainup.commons.domain.enums.Role;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "roles")

public class RoleDocument {

    @Id
    private String id;

    @Field("name")
    private Role name; // PT, CUSTOMER, ADMIN

    @Field("permissions")
    private Set<Permission> permissions; // danh sách quyền của role này
}
