package chess.controller;

import chess.AppSession;
import chess.Main;
import chess.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

import java.io.IOException;

public class MainMenuController {
    @FXML private Label welcomeLabel;
    @FXML private Button playPvPBtn;
    @FXML private Button playAIBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;
    @FXML private ComboBox<String> difficultyBox;
    @FXML private ComboBox<String> sideBox;

    // Called by FXMLLoader
    public void initialize() {
        User current = AppSession.getCurrentUser();
        if (current != null) {
            welcomeLabel.setText("Welcome, " + current.getDisplayName() + "!");
        }
        difficultyBox.getItems().addAll("Easy", "Medium", "Hard");
        difficultyBox.setValue("Medium");
        sideBox.getItems().addAll("Play as White", "Play as Black");
        sideBox.setValue("Play as White");
    }
    @FXML
    private void onPlayPvP(ActionEvent event) {
        // two-player human vs. human
        try {
            Main.startMatch(true, 0, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onPlayAI(ActionEvent event) {
        // human vs. AI
        try {
            // search depth = plies the computer looks ahead
            int depth = difficultyBox.getSelectionModel().getSelectedIndex() + 1;
            Main.startMatch(false, depth, sideBox.getSelectionModel().getSelectedIndex() == 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onSettings() {
        Main.showSettings();
    }

    @FXML
    private void onLogout() {
        AppSession.logout();
        Main.showLogin();
    }


}
