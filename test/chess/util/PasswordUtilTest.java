package chess.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void verifySucceedsForCorrectPassword() {
        String hash = PasswordUtil.hash("correct horse battery staple");
        assertTrue(PasswordUtil.verify("correct horse battery staple", hash));
    }

    @Test
    void verifyFailsForWrongPassword() {
        String hash = PasswordUtil.hash("correct horse battery staple");
        assertFalse(PasswordUtil.verify("wrong password", hash));
    }

    @Test
    void hashIsSaltedDifferentlyEachTime() {
        String a = PasswordUtil.hash("same password");
        String b = PasswordUtil.hash("same password");
        assertNotEquals(a, b, "BCrypt should salt each hash uniquely");
    }
}
