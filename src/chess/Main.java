package chess;

import chess.model.Board;
import chess.model.GameState;
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
import java.util.List;

public class Main extends Application {
    private static Stage primaryStage;


    @Override
    public void start(Stage stage) {
        // JavaFX entry point: window + login/menu
        applySettings();
        primaryStage = stage;
        primaryStage.setTitle("Chess Game");
        showLogin();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void startMatch(boolean twoPlayerMode) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/resources/Board.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        GameController ctrl = loader.getController();
        ctrl.initGame(new Board(), List.of(), true, twoPlayerMode);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

//
//    // For login/signup (they share login.css):
//    loadScene("/fxml/Login.fxml", 400, 320, "login.css");
//
//    // For main menu:
//    loadScene("/fxml/MainMenu.fxml", 600, 400, "menu.css");
//
//    // For board:
//    loadScene("/fxml/Board.fxml", 900, 700, "board.css");
//
//    // For settings:
//    loadScene("/fxml/Settings.fxml", 500, 400, "settings.css");

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
//    private static void loadScene(String fxmlPath,
//                                  int w, int h,
//                                  String... extraCss) throws IOException {
//        FXMLLoader loader = new FXMLLoader(
//                Main.class.getResource(fxmlPath)
//        );
//        Parent root = loader.load();
//        Scene scene = new Scene(root, w, h);
//
//        // global styles
//        scene.getStylesheets().add(
//                Main.class.getResource("/css/styles.css")
//                        .toExternalForm()
//        );
//
//        // page-specific styles
//        for (String css : extraCss) {
//            scene.getStylesheets().add(
//                    Main.class.getResource("/css/" + css).toExternalForm()
//            );
//        }
//
//        primaryStage.setScene(scene);
//        primaryStage.centerOnScreen();
//    }

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

