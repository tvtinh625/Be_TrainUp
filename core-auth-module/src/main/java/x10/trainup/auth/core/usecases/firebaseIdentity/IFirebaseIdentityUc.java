package x10.trainup.auth.core.usecases.firebaseIdentity;

public interface IFirebaseIdentityUc {
    FirebaseIdentityUcResp process(FirebaseSignUpUcRequest req);
}
