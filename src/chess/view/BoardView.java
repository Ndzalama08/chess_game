package chess.view;

import chess.ClickHandler;
import chess.model.Piece;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
/**
 * BoardView handles rendering and orientation of the chess board.
 */
public class BoardView {
    private final GridPane grid;
    private final double tileSize = 80;
    private boolean flipped = false;
    private ClickHandler handler;

    public BoardView(GridPane gridPane, ClickHandler clickHandler) {
        this.grid = gridPane;
        this.handler = clickHandler;
    }

    public void update(Piece[][] boardState) {
        grid.getChildren().clear();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {

                int row = flipped ? 7 - r : r;
                int col = flipped ? 7 - c : c;

                StackPane cell = new StackPane();
                cell.setPrefSize(tileSize, tileSize);
                Rectangle bg = new Rectangle(tileSize, tileSize,
                        ((row + col) % 2 == 0) ? Color.BEIGE : Color.SADDLEBROWN);
                cell.getChildren().add(bg);

                Piece p = boardState[row][col];
                if (p != null) {
                    ImageView img = p.getPieceImage();
                    img.setFitWidth(tileSize * 0.8);
                    img.setFitHeight(tileSize * 0.8);
                    cell.getChildren().add(img);
                }

                // Attach your click handler here:
                int rr = row, cc = col;
                cell.setOnMouseClicked(e -> handler.onClick(rr, cc, cell));

                grid.add(cell, c, r);
            }
        }
    }
    /**
     * Flips the board orientation (for two-player mode).
     */
    public void flip() {
        flipped = !flipped;
        updateOrientation();
    }

    /**
     * Applies rotation to the grid for smooth flip effect (optional).
     */
    private void updateOrientation() {
        grid.setRotate(flipped ? 180 : 0);
    }

    /**
     * Resets orientation to default (white at bottom).
     */
    public void reset() {
        if (flipped) {
            flipped = false;
            updateOrientation();
        }
    }
}
