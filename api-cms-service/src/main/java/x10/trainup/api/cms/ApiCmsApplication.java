package x10.trainup.api.cms;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = "x10.trainup")
@EnableMongoRepositories(basePackages = "x10.trainup")
public class ApiCmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiCmsApplication.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

    }
}