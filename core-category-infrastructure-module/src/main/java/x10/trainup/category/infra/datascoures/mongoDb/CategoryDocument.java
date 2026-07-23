package x10.trainup.category.infra.datascoures.mongoDb;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Document(collection = "categories")
public class CategoryDocument {

    @Id
    private String id;
    private String name;
    private String description;
    private boolean isActive;
}