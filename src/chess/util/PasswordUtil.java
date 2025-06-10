package chess.util;

import java.security.MessageDigest;

public class PasswordUtil {
    public static String hash(String pw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(pw.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte x: b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public static boolean verify(String pw, String hash) {
        return hash(pw).equals(hash);
    }
}

