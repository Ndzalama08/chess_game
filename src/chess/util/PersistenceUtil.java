package chess.util;

import chess.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.scenario.Settings;
import javafx.scene.paint.Color;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class PersistenceUtil {
    private static final File USER_FILE = new File("users.json");
    private static Map<String,User> users = new HashMap<>();
    private static final Gson gson = new Gson();

    private static Settings settings;
    private static final File SETTINGS_FILE = new File("settings.json");

    static {
        loadSettings();
    }
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


    public static class Settings {
        public String lightTileColor = "#f0d9b5";
        public String darkTileColor = "#b58863";
        public int timePerPlayer = 10;
        public String pieceStyle = "Classic";
        public boolean soundsEnabled = true;
    }
    private static void loadSettings() {
        if (!SETTINGS_FILE.exists()) {
            settings = new Settings();            // ensure non‐null
            saveSettings(settings);               // write defaults
            return;
        }
        try (Reader r = new FileReader(SETTINGS_FILE)) {
            Settings loaded = new Gson().fromJson(r, Settings.class);
            settings = (loaded != null) ? loaded : new Settings();
        } catch (Exception e) {
            // on any error, fall back to defaults
            settings = new Settings();
        }
    }

    public static Settings getSettings() {
        // never return null
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    public static void saveSettings(Settings s) {
        settings = s;
        try (Writer w = new FileWriter(SETTINGS_FILE)) {
            new Gson().toJson(settings, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
