package chess.model;

public class User {
    private String username;
    private String passwordHash;  // store hashed password
    private String displayName;   // e.g. “John Doe”

    public User(String username, String passwordHash, String displayName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
    }
    // getters/setters omitted for brevity
}

