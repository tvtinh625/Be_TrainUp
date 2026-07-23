package x10.trainup.auth.core.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initializeFirebaseApp(
            @Value("${firebase.authentication.key.file}") String firebaseKeyFile) throws IOException {

        InputStream serviceAccount;

        if (firebaseKeyFile.startsWith("classpath:")) {
            String path = firebaseKeyFile.replace("classpath:", "");
            serviceAccount = getClass().getClassLoader().getResourceAsStream(path);
            if (serviceAccount == null) {
                throw new IOException("Không tìm thấy file Firebase key trong classpath: " + path);
            }
        } else {
            serviceAccount = new java.io.FileInputStream(firebaseKeyFile);
        }

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }
}
