package chess.util;

import chess.model.User;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * users.json is a plain file on disk that anyone can truncate or hand-edit.
 * Parsing it must never hand back null or throw, since the result populates a
 * static field used by every login.
 */
class PersistenceUtilTest {

    @Test
    void parsesAStoredUserMap() {
        Map<String, User> users = PersistenceUtil.parseUsers(
                new StringReader("{\"ndzalama\":{\"username\":\"ndzalama\",\"passwordHash\":\"abc\"}}"));

        assertNotNull(users);
        assertTrue(users.containsKey("ndzalama"));
    }

    @Test
    void emptyFileYieldsAnEmptyMap() {
        Map<String, User> users = PersistenceUtil.parseUsers(new StringReader(""));

        assertNotNull(users, "an empty file must not produce a null map");
        assertTrue(users.isEmpty());
    }

    @Test
    void jsonNullYieldsAnEmptyMap() {
        assertTrue(PersistenceUtil.parseUsers(new StringReader("null")).isEmpty());
    }

    @Test
    void malformedJsonYieldsAnEmptyMapInsteadOfThrowing() {
        Map<String, User> users = assertDoesNotThrow(() ->
                PersistenceUtil.parseUsers(new StringReader("{not valid json")));

        assertTrue(users.isEmpty());
    }
}
