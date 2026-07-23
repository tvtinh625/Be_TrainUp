package x10.trainup.auth.core.firebase;

import com.google.firebase.auth.FirebaseToken;

public interface IFirebaseService {
    FirebaseToken verifyToken(String idToken);
}
