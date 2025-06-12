package chess.controller;

import chess.AppSession;
import chess.Main;
import chess.model.User;
import chess.util.PersistenceUtil;
import chess.util.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void onLogin() {

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
        AppSession.login(user);
        Main.showMainMenu();
    }

    @FXML
    private void onShowSignup() {
        Main.showSignup();
    }
}
