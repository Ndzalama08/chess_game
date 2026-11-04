package chess.controller;

import chess.model.Board;
import chess.util.SoundManager;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Loads every screen through the real FXML files (catches wrong resource
 * paths and fx:id typos) and plays one move against the AI. Skipped when no
 * display is available; CI runs it under xvfb.
 */
class UiSmokeTest {

    @BeforeAll
    static void startToolkit() {
        SoundManager.setEnabled(false);
        boolean up;
        try {
            Platform.startup(() -> {});
            up = true;
        } catch (IllegalStateException alreadyRunning) {
            up = true;
        } catch (Throwable noDisplay) {
            up = false;
        }
        assumeTrue(up, "JavaFX toolkit not available");
    }

    static <T> T onFx(Callable<T> job) throws Exception {
        CompletableFuture<T> result = new CompletableFuture<>();
        Platform.runLater(() -> {
            try { result.complete(job.call()); } catch (Throwable t) { result.completeExceptionally(t); }
        });
        return result.get(10, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Test
    void everyScreenLoads() throws Exception {
        for (String fxml : new String[]{"/login.fxml", "/signup.fxml", "/mainMenu.fxml", "/Settings.fxml", "/board.fxml"}) {
            assertNotNull(getClass().getResource(fxml), fxml + " missing from classpath");
            onFx(() -> new FXMLLoader(getClass().getResource(fxml)).load());
        }
        assertNotNull(getClass().getResource("/styles.css"));
        assertNotNull(getClass().getResource("/sounds/move.wav"));
        assertNotNull(getClass().getResource("/images/classic/white_king.png"));
    }

    private static void click(GridPane grid, int row, int col) {
        for (Node cell : grid.getChildren()) {
            if (GridPane.getRowIndex(cell) == row && GridPane.getColumnIndex(cell) == col) {
                Event.fireEvent(cell, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0,
                        MouseButton.PRIMARY, 1, false, false, false, false,
                        true, false, false, true, false, false, null));
                return;
            }
        }
        fail("no cell at " + row + "," + col);
    }

    @Test
    void humanMoveGetsAnAiReply() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/board.fxml"));
        Object root = onFx(loader::load);
        assertNotNull(root);
        GameController ctrl = loader.getController();
        onFx(() -> { ctrl.initGame(new Board(), true, false, 1, true); return null; });

        GridPane grid = (GridPane) lookup(loader, "chessBoard");
        ListView<?> history = (ListView<?>) lookup(loader, "historyList");

        onFx(() -> { click(grid, 6, 4); return null; });   // select e2
        onFx(() -> { click(grid, 4, 4); return null; });   // e2-e4

        // the AI answers from a background thread
        long deadline = System.currentTimeMillis() + 10_000;
        while (onFx(() -> history.getItems().size()) < 2 && System.currentTimeMillis() < deadline) {
            Thread.sleep(50);
        }
        assertEquals(2, (int) onFx(() -> history.getItems().size()), "human move + AI reply");
    }

    private static Object lookup(FXMLLoader loader, String id) throws Exception {
        return onFx(() -> ((javafx.scene.Parent) loader.getRoot()).lookup("#" + id));
    }
}
