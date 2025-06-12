package chess.controller;

import chess.Main;
import chess.logic.GameManager;
import chess.model.Board;
import chess.model.Move;
import chess.model.Piece;
import chess.view.BoardView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GameController {
    @FXML
    private GridPane chessBoard;
    private BoardView boardView;
    // fx:id="historyPane"
    @FXML
    private Label whiteTimerLabel, blackTimerLabel;
    @FXML
    private ListView<String> historyList;

    private Timeline timer;
    private Duration whiteTime = Duration.minutes(10);
    private Duration blackTime = Duration.minutes(10);

    private final Board board = new Board();
    private final GameManager gameManager = new GameManager(board);

    private Piece selectedPiece = null;
    private int selectedRow = -1, selectedCol = -1;
    private boolean twoPlayerMode = true;



    @FXML
    public void initialize() {

        boardView = new BoardView(chessBoard, this::handleClick);
        boardView.update(board.getBoard());
        resetTimers();
        historyList.getItems().clear();


        // Setup timer to tick every second
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    // timer methods
    private void tick() {
        if (gameManager.isWhiteTurn()) {
            whiteTime = whiteTime.subtract(Duration.seconds(1));
            updateLabel(whiteTimerLabel, "White", whiteTime);
            if (whiteTime.lessThanOrEqualTo(Duration.ZERO)) timeExpired(false);
        } else {
            blackTime = blackTime.subtract(Duration.seconds(1));
            updateLabel(blackTimerLabel, "Black", blackTime);
            if (blackTime.lessThanOrEqualTo(Duration.ZERO)) timeExpired(true);
        }
    }

    private void updateLabel(Label lbl, String name, Duration time) {
        long minutes = (long) time.toMinutes();
        long seconds = (long) (time.toSeconds() % 60);
        lbl.setText(String.format("%s: %02d:%02d", name, minutes, seconds));
    }

    private void timeExpired(boolean whiteExpired) {
        timer.stop();
        String winner = whiteExpired ? "Black" : "White";
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                winner + " wins on time!", ButtonType.OK);
        alert.showAndWait();
        Main.showMainMenu();
    }

    private void resetTimers() {
        whiteTime = Duration.minutes(10);
        blackTime = Duration.minutes(10);
        updateLabel(whiteTimerLabel, "White", whiteTime);
        updateLabel(blackTimerLabel, "Black", blackTime);
    }



    private void handleClick(int row, int col, StackPane square) {
        Piece clicked = board.getBoard()[row][col];

        if (selectedPiece == null) {
            // 1) Select a piece
            if (clicked != null && clicked.isWhite() == gameManager.isWhiteTurn()) {
                selectedPiece = clicked;
                selectedRow = row;
                selectedCol = col;
                // highlight
                square.setStyle(square.getStyle()
                        + "-fx-border-color: yellow; -fx-border-width: 3;");
            }
        } else {
            // 2) Try move via GameManager (runs validation + execution)
            Move move = new Move(selectedRow, selectedCol, row, col);
            boolean moved = gameManager.attemptMove(move);

            if (moved) {
                //  Valid: redraw and record history
                boardView.update(board.getBoard());

                if (twoPlayerMode) {
                    boardView.flip();
                }

                // record history
                historyList.getItems().add(Move.notation(move));
                historyList.scrollTo(historyList.getItems().size() - 1);
            } else {
                // 3b) Invalid: flash red
                flashRed(square);
            }

            // 4) Reset selection (after move or flash)
            selectedPiece = null;
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    private void flashRed(StackPane square) {
        // Overlay sits on top of the square only temporarily
        Rectangle overlay = new Rectangle(80, 80);
        overlay.setFill(Color.rgb(255, 0, 0, 0.5));
        square.getChildren().add(overlay);

        PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
        pause.setOnFinished(e -> square.getChildren().remove(overlay));
        pause.play();
    }

}
