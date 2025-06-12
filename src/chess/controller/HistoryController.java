package chess.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class HistoryController {
    @FXML
    private ListView<String> historyList;

    /** Appends a move in algebraic (e2→e4) notation */
    public void addMove(String notation) {
        historyList.getItems().add(notation);
        // scroll to last
        historyList.scrollTo(historyList.getItems().size() - 1);
    }

    /** Clears history for a new game */
    public void clear() {
        historyList.getItems().clear();
    }
}
