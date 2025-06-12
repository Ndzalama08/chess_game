// GameController.java
package chess.controller;

import chess.Main;
import chess.logic.ChessAI;
import chess.logic.GameManager;
import chess.model.Board;
import chess.model.Move;
import chess.model.Piece;
import chess.util.SoundManager;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;

public class GameController {
    @FXML private GridPane chessBoard;
    @FXML private Label whiteTimerLabel, blackTimerLabel;
    @FXML private ListView<String> historyList;


    private BoardView boardView;
    private final Board board = new Board();
    private GameManager gameManager = new GameManager(board);
    private Piece selectedPiece;
    private int selectedRow = -1, selectedCol = -1;
    private boolean twoPlayerMode = true;
    private boolean vsAi;
    private int aiDepth = 2;  // easy difficulty default

    private Timeline timer;
    private Duration whiteTime;
    private Duration blackTime;

    private static int timePerPlayerMinutes = 10;

    /**
     * Initializes the game controller.
     * @param board       the starting board
     * @param history     list of moves
     * @param whiteToMove who starts
     * @param twoPlayer   true=two humans; false=human vs AI
     */
    @FXML
    public void initGame(Board board,
                         List<String> history,
                         boolean whiteToMove,
                         boolean twoPlayer) {
        this.twoPlayerMode = twoPlayer;
        this.gameManager   = new GameManager(board);
        this.gameManager.setWhiteTurn(whiteToMove);

        // Apply saved settings first
        whiteTime = Duration.minutes(timePerPlayerMinutes);
        blackTime = Duration.minutes(timePerPlayerMinutes);
        updateLabel(whiteTimerLabel, "White", whiteTime);
        updateLabel(blackTimerLabel, "Black", blackTime);

        // Initialize board view with click callback
        boardView = new BoardView(chessBoard, this::handleClick);
        boardView.update(board.getBoard());
        historyList.getItems().clear();

        // Start a ticking clock
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    public static void setTimePerPlayer(int minutes) {
        timePerPlayerMinutes = minutes;
    }

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
        long mins = (long) time.toMinutes();
        long secs = (long) (time.toSeconds() % 60);
        lbl.setText(String.format("%s: %02d:%02d", name, mins, secs));
    }

    private void timeExpired(boolean whiteExpired) {
        timer.stop();
        String winner = whiteExpired ? "Black" : "White";
        Alert alert = new Alert(Alert.AlertType.INFORMATION,
                winner + " wins on time!", ButtonType.OK);
        alert.showAndWait();
        Main.showMainMenu();
    }

    private void handleClick(int row, int col, StackPane cell) {
        Piece clicked = board.getBoard()[row][col];
        if (selectedPiece == null) {
            if (clicked != null && clicked.isWhite() == gameManager.isWhiteTurn()) {
                selectedPiece = clicked;
                selectedRow = row;
                selectedCol = col;
                cell.setStyle(cell.getStyle() + "-fx-border-color: yellow; -fx-border-width: 3;");
            }
        } else {
            Move move = new Move(selectedRow, selectedCol, row, col);
            boolean moved = gameManager.attemptMove(move);



            if (moved) {
                // flip for PvP
                if (twoPlayerMode) {

                    if (gameManager.isWhiteTurn()) {
                        // White to move → reset back to White at bottom
                        boardView.reset();
                    }else {
                        boardView.flip();
                    }
                    boardView.update(board.getBoard());
                }
                if (vsAi && !gameManager.isWhiteTurn()) {
                    Move aiMove = new ChessAI(aiDepth).findBestMove(gameManager);
                    gameManager.attemptMove(aiMove);
                    boardView.update(gameManager.getBoardWrapper().getBoard());
                    historyList.getItems().add(Move.notation(aiMove));
                }

                // record move
                historyList.getItems().add(Move.notation(move));
                historyList.scrollTo(historyList.getItems().size() - 1);
                // move sound
                SoundManager.play("/resources/sounds/move.wav");

                if (gameManager.isInCheck(!gameManager.isWhiteTurn())) {
                    // after the move, if opponent is now in check
                    SoundManager.play("/sounds/check.wav");
                }
                boardView.flip();


                if (gameManager.isCheckmate()) {
                    SoundManager.play("/sounds/checkmate.wav");
                    String winner = gameManager.isWhiteTurn() ? "Black" : "White";
                    Alert alert = new Alert(Alert.AlertType.INFORMATION,
                            winner + " wins by checkmate!", ButtonType.OK);
                    alert.showAndWait();
                    Main.showMainMenu();
                    return;
                }

            } else {
                flashRed(cell);
                SoundManager.play("/resources/sounds/illegal.wav");
            }
            selectedPiece = null;
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    private void flashRed(StackPane cell) {
        Rectangle overlay = new Rectangle(80, 80, Color.rgb(255,0,0,0.5));
        cell.getChildren().add(overlay);
        PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
        pause.setOnFinished(e -> cell.getChildren().remove(overlay));
        pause.play();
    }

}
