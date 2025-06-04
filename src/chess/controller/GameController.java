package chess.controller;

import chess.model.Board;
import chess.model.Move;
import chess.model.Piece;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GameController {
    @FXML
    private GridPane chessBoard;
    private final Board board = new Board();

    private Piece selectedPiece = null;
    private int selectedRow = -1, selectedCol = -1;

    public void initialize() {
        drawBoard();
    }

    private void drawBoard() {
        chessBoard.getChildren().clear();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane square = new StackPane();
                square.setPrefSize(80, 80);

                String color = (row + col) % 2 == 0 ? "#f0d9b5" : "#b58863";
                square.setStyle("-fx-background-color: " + color + ";");

                Piece piece = board.getBoard()[row][col];
                if (piece != null) {
                    ImageView image = piece.getPieceImage();
                    image.setFitWidth(60);
                    image.setFitHeight(60);
                    square.getChildren().add(image);
                }

                int r = row, c = col;
                square.setOnMouseClicked(e -> handleClick(r, c, square));

                chessBoard.add(square, col, row);
            }
        }
    }

    private void handleClick(int row, int col, StackPane square) {
        Piece clickedPiece = board.getBoard()[row][col];

        if (selectedPiece == null) {
            if (clickedPiece != null) {
                selectedPiece = clickedPiece;
                selectedRow = row;
                selectedCol = col;
                square.setStyle(square.getStyle() + "-fx-border-color: yellow; -fx-border-width: 3;");
            }
        } else {
            if (selectedPiece.isValidMove(new Move(selectedRow, selectedCol, row, col), board.getBoard())) {
                board.getBoard()[row][col] = selectedPiece;
                board.getBoard()[selectedRow][selectedCol] = null;
            } else {
                flashRed(square);
            }

            selectedPiece = null;
            selectedRow = -1;
            selectedCol = -1;
            drawBoard();
        }
    }

    private void flashRed(StackPane square) {
        Rectangle overlay = new Rectangle(80, 80);
        overlay.setFill(Color.rgb(255, 0, 0, 0.5));
        square.getChildren().add(overlay);

        PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
        pause.setOnFinished(e -> square.getChildren().remove(overlay));
        pause.play();
    }
}
