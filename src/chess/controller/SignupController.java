package chess.controller;

import chess.Main;
import chess.model.User;
import chess.util.PersistenceUtil;
import chess.util.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignupController {
    @FXML private TextField displayNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void onSignup() {
        String name = displayNameField.getText().trim();
        String u    = usernameField.getText().trim();
        String p    = passwordField.getText();

        if (name.isEmpty() || u.isEmpty() || p.isEmpty()) {
            errorLabel.setText("All fields required");
            return;
        }
        if (PersistenceUtil.usernameExists(u)) {
            errorLabel.setText("Username taken");
            return;
        }
        String hash = PasswordUtil.hash(p);
        User user = new User(u, hash, name);
        PersistenceUtil.addUser(user);
        errorLabel.setText("Account created! Please log in.");
    }

    @FXML
    private void onShowLogin() {
        Main.showLogin();
    }
}
