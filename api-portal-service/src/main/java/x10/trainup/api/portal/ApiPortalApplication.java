package x10.trainup.api.portal;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = "x10.trainup")
@EnableMongoRepositories(basePackages = "x10.trainup")
public class ApiPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiPortalApplication.class, args);
    }
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }
}

