package chess;

import chess.model.User;

public final class AppSession {
    private static User currentUser = null;

    // Prevent instantiation
    private AppSession() {}

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }


}
