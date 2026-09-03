package chess.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    public static String hash(String pw) {
        return BCrypt.hashpw(pw, BCrypt.gensalt());
    }

    public static boolean verify(String pw, String hash) {
        return BCrypt.checkpw(pw, hash);
    }
}
