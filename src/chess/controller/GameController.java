package chess.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GameController {
    @FXML
    private GridPane chessBoard;

    public void initialize() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane square = new StackPane();
                square.setPrefSize(80, 80); // Size of each square

                // Color alternation logic
                String color = (row + col) % 2 == 0 ? "#f0d9b5" : "#b58863"; // Light and dark squares
                square.setStyle("-fx-background-color: " + color + ";");

                // Optional: assign coordinates as IDs
                square.setId(row + "," + col);

                chessBoard.add(square, col, row); // Add to GridPane
            }
        }
    }

}
