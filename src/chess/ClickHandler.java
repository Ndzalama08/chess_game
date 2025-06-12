package chess;

import javafx.scene.layout.StackPane;

public interface ClickHandler {
    void onClick(int row, int col, StackPane cell);
}

