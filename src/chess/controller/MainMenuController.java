package chess.controller;

import chess.AppSession;
import chess.Main;
import chess.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class MainMenuController {
    @FXML private Label welcomeLabel;
    @FXML private Button playPvPBtn;
    @FXML private Button playAIBtn;
    @FXML private Button settingsBtn;
    @FXML private Button logoutBtn;

    // Called by FXMLLoader
    public void initialize() {
        User current = AppSession.getCurrentUser();
        if (current != null) {
            welcomeLabel.setText("Welcome, " + current.getDisplayName() + "!");
        }
    }

    @FXML
    private void onPlayPvP() {
        Main.showBoard();       // assumes BoardController handles PvP and flipping
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

    @FXML
    public void onPlayAI(ActionEvent e) throws IOException {
        Main.startGame(false);
    }
}
