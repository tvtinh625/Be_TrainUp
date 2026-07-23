package x10.trainup.auth.core.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {


    public FirebaseToken verifyIdToken(String idToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return decodedToken;
        } catch (Exception e) {
            throw new RuntimeException("❌ ID Token không hợp lệ hoặc đã hết hạn: " + e.getMessage());
        }
    }
}
