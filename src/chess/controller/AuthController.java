package chess.controller;

import chess.model.User;
import chess.*;
import chess.util.PersistenceUtil;
import chess.util.PasswordUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AuthController {
    @FXML private TextField usernameField, displayNameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    public void onLogin() {
        String u = usernameField.getText().trim();
        String p = passwordField.getText();
        if (!PersistenceUtil.usernameExists(u)) {
            errorLabel.setText("No such user");
            return;
        }
        User user = PersistenceUtil.getUser(u);
        if (!PasswordUtil.verify(p, user.getPasswordHash())) {
            errorLabel.setText("Wrong password");
            return;
        }
        // success: store user session and go to main menu
        AppSession.login(user);
        Main.showMainMenu();
    }

    public void onSignup() {
        String name = displayNameField.getText().trim();
        String u = usernameField.getText().trim();
        String p = passwordField.getText();
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

    // methods showSignup() and showLogin() switch FXML scenes...

    public void showLogin(ActionEvent actionEvent) {

    }

    public void showSignup(ActionEvent actionEvent) {

    }


}
