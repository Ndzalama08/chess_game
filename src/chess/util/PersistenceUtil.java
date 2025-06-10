package chess.util;

import chess.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class PersistenceUtil {
    private static final File USER_FILE = new File("users.json");
    private static Map<String,User> users = new HashMap<>();
    private static final Gson gson = new Gson();

    static {
        loadUsers();
    }

    //private constructor so it cant be initialized
    private PersistenceUtil() {}

    private static void loadUsers() {
        if (!USER_FILE.exists()) return;
        try (Reader r = new FileReader(USER_FILE)) {
            Type type = new TypeToken<Map<String,User>>(){}.getType();
            users = gson.fromJson(r, type);
        } catch (IOException e) { /* ignore for now */ }
    }

    private static void saveUsers() {
        try (Writer w = new FileWriter(USER_FILE)) {
            gson.toJson(users, w);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static boolean usernameExists(String username) {
        return users.containsKey(username);
    }

    public static void addUser(User u) {
        users.put(u.getUsername(), u);
        saveUsers();
    }

    public static User getUser(String username) {
        return users.get(username);
    }
}
