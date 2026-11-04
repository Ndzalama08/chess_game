package chess;

/**
 * Entry point for the packaged jar. Main extends Application, and the JVM
 * refuses to launch an Application subclass from a classpath jar that does
 * not have the JavaFX modules on the module path; going through a plain class
 * avoids that check.
 */
public final class Launcher {
    private Launcher() {}

    public static void main(String[] args) {
        Main.main(args);
    }
}
