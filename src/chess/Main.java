package chess;

import chess.util.PersistenceUtil;
import chess.controller.GameController;
import chess.util.SoundManager;
import chess.view.BoardView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private static Stage primaryStage;


    @Override
    public void start(Stage stage) throws Exception {
        applySettings();
        primaryStage = stage;
        primaryStage.setTitle("Chess Game");
        showLogin();            // start at login screen
        System.out.println("Attempting sound: " + SoundManager.class.getResource("resources/sounds/move.wav"));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Load and display login.fxml
    public static void showLogin() {
        loadScene("/resources/login.fxml", 400, 300);
    }

    // Load and display signup.fxml
    public static void showSignup() {
        loadScene("/resources/signup.fxml", 400, 350);
    }

    // Load and display main_menu.fxml
    public static void showMainMenu() {
        loadScene("/mainMenu.fxml", 600, 400);
    }

    // Load and display board.fxml
    public static void showBoard() {
        loadScene("/resources/board.fxml", 700, 700);
    }

    // Load and display settings.fxml
    public static void showSettings() {
        loadScene("/resources/Settings.fxml", 500, 400);
    }

    public static void applySettings() {
        var s = PersistenceUtil.getSettings();

        // 1) update board visuals & piece style
        Color light = Color.web(s.lightTileColor);
        Color dark = Color.web(s.darkTileColor);
        BoardView.setTileColors(light, dark);
        BoardView.setPieceStyle(s.pieceStyle);

        // 2) update the time per player
        GameController.setTimePerPlayer(s.timePerPlayer);
        // 3) update sound on/off
        SoundManager.setEnabled(s.soundsEnabled);

    }

    // Generic helper to switch scenes
    private static void loadScene(String fxmlPath, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(Main.class.getResource("/resources/styles.css").toExternalForm());
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

