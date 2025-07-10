package chess.controller;

import chess.AppSession;
import chess.Main;
import chess.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

import java.io.IOException;

public class MainMenuController {
    @FXML private Label welcomeLabel;

    // Called by FXMLLoader
    public void initialize() {
        User current = AppSession.getCurrentUser();
        if (current != null) {
            welcomeLabel.setText("Welcome, " + current.getDisplayName() + "!");
        }
    }
    @FXML
    private void onPlayPvP(ActionEvent event) {
        // two-player human vs. human
        try {
            Main.startMatch(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onPlayAI(ActionEvent event) {
        // human vs. AI
        try {
            Main.startMatch(false);
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
