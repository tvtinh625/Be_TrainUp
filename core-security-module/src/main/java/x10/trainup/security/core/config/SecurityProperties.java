package x10.trainup.security.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {

    private Cors cors = new Cors();
    private List<String> publicUrls;
    private List<ProtectedUrl> protectedUrls;

    @Data
    public static class Cors {
        private boolean enabled;
        private String allowedOrigins;
        private String allowedMethods;
        private String allowedHeaders;
    }

    @Data
    public static class ProtectedUrl {
        private String urlPattern;
        private List<String> roles;
    }
}
