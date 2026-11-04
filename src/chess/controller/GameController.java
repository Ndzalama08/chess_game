// GameController.java
package chess.controller;

import chess.Main;
import chess.logic.ChessAI;
import chess.logic.ChessClock;
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
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class GameController {
    private static final Color SELECTED = Color.rgb(255, 215, 0, 0.55);
    private static final Color TARGET   = Color.rgb(50, 160, 70, 0.45);
    private static final Color LAST     = Color.rgb(255, 235, 59, 0.35);

    @FXML private GridPane chessBoard;
    @FXML private Label whiteTimerLabel, blackTimerLabel, statusLabel;
    @FXML private ListView<String> historyList;

    private BoardView boardView;
    private GameManager gameManager;
    private int selectedRow = -1, selectedCol = -1;
    private List<Move> selectedMoves = List.of();
    private Move lastMove;

    private boolean twoPlayerMode = true;
    private boolean humanWhite = true;   // PvAI only: the side the human plays
    private int aiDepth = 2;
    private boolean aiThinking;
    private boolean gameOver;

    private Timeline timer;
    private ChessClock clock;

    private static int timePerPlayerMinutes = 10;

    /**
     * Initializes the game controller.
     * @param board       the starting board
     * @param whiteToMove who starts
     * @param twoPlayer   true=two humans; false=human vs. AI
     * @param aiDepth     AI search depth (ignored for two players)
     * @param humanWhite  PvAI: true if the human plays White
     */
    @FXML
    public void initGame(Board board,
                         boolean whiteToMove,
                         boolean twoPlayer,
                         int aiDepth,
                         boolean humanWhite) {
        this.twoPlayerMode = twoPlayer;
        this.aiDepth       = aiDepth;
        this.humanWhite    = humanWhite;
        this.gameManager   = new GameManager(board);
        this.gameManager.setWhiteTurn(whiteToMove);
        // The AI promotes to a queen without asking.
        this.gameManager.setPromotionChooser(isWhite -> {
            if (!twoPlayerMode && isWhite != humanWhite) return "Queen";
            ChoiceDialog<String> dlg = new ChoiceDialog<>("Queen",
                    List.of("Queen", "Rook", "Bishop", "Knight"));
            dlg.setTitle("Pawn Promotion");
            dlg.setHeaderText("Choose a piece to promote to:");
            return dlg.showAndWait().orElse("Queen");
        });

        clock = new ChessClock(timePerPlayerMinutes);
        updateClockLabels();

        boardView = new BoardView(chessBoard, this::handleClick);
        historyList.getItems().clear();
        refresh();

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();

        startAiIfItsTurn();
    }

    public static void setTimePerPlayer(int minutes) {
        timePerPlayerMinutes = minutes;
    }

    private void tick() {
        boolean whiteToMove = gameManager.isWhiteTurn();
        // tick() reports on the side it charged, so the loser can't be mixed up
        // the way it was when the two branches passed a hand-written flag.
        boolean flagFell = clock.tick(whiteToMove);
        updateClockLabels();
        if (flagFell) {
            endGame(ChessClock.winnerOnTime(whiteToMove) + " wins on time!");
        }
    }

    private void updateClockLabels() {
        whiteTimerLabel.setText("White: " + ChessClock.format(clock.getWhiteSeconds()));
        blackTimerLabel.setText("Black: " + ChessClock.format(clock.getBlackSeconds()));
    }

    /** Idempotent: the clock must not outlive the screen that owns it. */
    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    /** Redraws the board with last-move, selection and legal-target markers. */
    private void refresh() {
        boardView.clearMarks();
        if (lastMove != null) {
            boardView.mark(lastMove.fromRow, lastMove.fromCol, LAST);
            boardView.mark(lastMove.toRow, lastMove.toCol, LAST);
        }
        if (selectedRow >= 0) {
            boardView.mark(selectedRow, selectedCol, SELECTED);
            for (Move m : selectedMoves) {
                boardView.mark(m.toRow, m.toCol, TARGET);
            }
        }
        // shared screen: whoever is to move sits at the bottom; vs AI: the human does
        boardView.setFlipped(twoPlayerMode ? !gameManager.isWhiteTurn() : !humanWhite);
        boardView.update(gameManager.getBoardWrapper().getBoard());
        statusLabel.setText(aiThinking ? "Computer is thinking…"
                : (gameManager.isWhiteTurn() ? "White to move" : "Black to move"));
    }

    private void select(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        selectedMoves = gameManager.generateLegalMoves(gameManager.isWhiteTurn()).stream()
                .filter(m -> m.fromRow == row && m.fromCol == col)
                .toList();
        refresh();
    }

    private void clearSelection() {
        selectedRow = selectedCol = -1;
        selectedMoves = List.of();
    }

    private void handleClick(int row, int col, StackPane cell) {
        if (gameOver || aiThinking) return;

        Piece clicked = gameManager.getBoardWrapper().getBoard()[row][col];

        // own piece: (re)select it
        if (clicked != null && clicked.isWhite() == gameManager.isWhiteTurn()) {
            select(row, col);
            return;
        }
        if (selectedRow < 0) return;

        if (applyMove(new Move(selectedRow, selectedCol, row, col))) {
            clearSelection();
            afterMove();
        } else {
            flashRed(cell);
            SoundManager.play("/sounds/illegal.wav");
        }
    }

    /** Plays the move and its sound/history. False when the move is illegal. */
    private boolean applyMove(Move move) {
        boolean capture = gameManager.getBoardWrapper().getBoard()[move.toRow][move.toCol] != null;
        if (!gameManager.attemptMove(move)) return false;

        lastMove = move;
        SoundManager.play(capture ? "/sounds/capture.wav" : "/sounds/move.wav");
        historyList.getItems().add(Move.notation(move));
        historyList.scrollTo(historyList.getItems().size() - 1);
        // the turn has flipped, so this is the side that was just attacked
        if (gameManager.isInCheck(gameManager.isWhiteTurn())) {
            SoundManager.play("/sounds/check.wav");
        }
        return true;
    }

    /** Shared tail of every move, human or AI: redraw, end-of-game check, AI reply. */
    private void afterMove() {
        refresh();
        if (gameManager.isCheckmate()) {
            SoundManager.play("/sounds/checkmate.wav");
            endGame((gameManager.isWhiteTurn() ? "Black" : "White") + " wins by checkmate!");
        } else if (gameManager.drawReason() != null) {
            endGame("Draw by " + gameManager.drawReason() + ".");
        } else {
            startAiIfItsTurn();
        }
    }

    private void startAiIfItsTurn() {
        if (twoPlayerMode || gameOver || gameManager.isWhiteTurn() == humanWhite) return;

        aiThinking = true;
        refresh();
        // The search runs on a copy: generateLegalMoves briefly flips the turn
        // on the manager it is called on, which the UI thread reads every tick.
        GameManager snapshot = gameManager.clone();
        Task<Move> search = new Task<>() {
            @Override protected Move call() {
                return new ChessAI(aiDepth).findBestMove(snapshot);
            }
        };
        search.setOnSucceeded(e -> {
            aiThinking = false;
            Move reply = search.getValue();
            if (gameOver || reply == null || !applyMove(reply)) return;
            afterMove();
        });
        search.setOnFailed(e -> {
            aiThinking = false;
            search.getException().printStackTrace();
        });
        Thread t = new Thread(search, "chess-ai");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void mainMenu() {
        gameOver = true;   // also discards a search still in flight
        stopTimer();
        Main.showMainMenu();
    }

    @FXML
    private void onResign() {
        if (gameOver) return;
        boolean resigningWhite = twoPlayerMode ? gameManager.isWhiteTurn() : humanWhite;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                (resigningWhite ? "White" : "Black") + " resigns?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            endGame((resigningWhite ? "White" : "Black") + " resigns. "
                    + (resigningWhite ? "Black" : "White") + " wins!");
        }
    }

    /**
     * Stops play and offers a rematch or the menu. Deferred with runLater
     * because JavaFX forbids showAndWait inside an animation (clock tick) or
     * task callback.
     */
    private void endGame(String message) {
        if (gameOver) return;
        gameOver = true;
        stopTimer();
        Platform.runLater(() -> {
            ButtonType rematch = new ButtonType("Rematch");
            ButtonType menu = new ButtonType("Main Menu");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, rematch, menu);
            alert.setHeaderText("Game over");
            if (alert.showAndWait().orElse(menu) == rematch) {
                try {
                    Main.startMatch(twoPlayerMode, aiDepth, humanWhite);
                    return;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            Main.showMainMenu();
        });
    }

    private void flashRed(StackPane cell) {
        Rectangle overlay = new Rectangle(80, 80, Color.rgb(255,0,0,0.5));
        cell.getChildren().add(overlay);
        PauseTransition pause = new PauseTransition(Duration.seconds(0.3));
        pause.setOnFinished(e -> cell.getChildren().remove(overlay));
        pause.play();
    }

}
