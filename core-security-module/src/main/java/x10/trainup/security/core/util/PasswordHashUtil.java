package x10.trainup.security.core.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHashUtil {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hash(String rawValue) {
        return encoder.encode(rawValue);
    }

    public static boolean verify(String rawValue, String hashedValue) {
        return encoder.matches(rawValue, hashedValue);
    }
}