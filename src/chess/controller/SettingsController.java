// SettingsController.java (src/chess/controller/SettingsController.java)
package chess.controller;

import chess.AppSession;
import chess.Main;
import chess.util.PersistenceUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

public class SettingsController {
    @FXML private ColorPicker lightColorPicker;
    @FXML private ColorPicker darkColorPicker;
    @FXML private Spinner<Integer> timeSpinner;
    @FXML private ComboBox<String> styleComboBox;
    @FXML private CheckBox soundCheckBox;

    @FXML
    public void initialize() {
        // Load saved settings
        var settings = PersistenceUtil.getSettings();
        lightColorPicker.setValue(Color.web(settings.lightTileColor));
        darkColorPicker.setValue(Color.web(settings.darkTileColor));

        timeSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1, 60, settings.timePerPlayer));
        styleComboBox.getItems().addAll("Classic", "Modern", "Wood");
        styleComboBox.setValue(settings.pieceStyle);
        soundCheckBox.setSelected(settings.soundsEnabled);
    }

    @FXML
    public void onSave() {
        var settings = PersistenceUtil.getSettings();
        settings.lightTileColor = lightColorPicker.getValue().toString();
        settings.darkTileColor = darkColorPicker.getValue().toString();
        settings.timePerPlayer = timeSpinner.getValue();
        settings.pieceStyle = styleComboBox.getValue();
        settings.soundsEnabled = soundCheckBox.isSelected();

        PersistenceUtil.saveSettings(settings);

        // Apply immediately
        Main.applySettings();
        Main.showMainMenu();
    }

    @FXML
    private void onCancel() {
        Main.showMainMenu();
    }
}

