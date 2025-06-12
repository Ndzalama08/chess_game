package chess.view;

import chess.model.Piece;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * BoardView handles drawing the chessboard and pieces,
 * and flips orientation without rotating piece images.
 */
public class BoardView {
    /** Callback for cell clicks */
    public interface ClickHandler {
        void onClick(int row, int col, StackPane cell);
    }

    private final GridPane grid;
    private final ClickHandler handler;
    private final double tileSize = 80;

    // Static settings applied via Main.applySettings()
    private static Color lightTile = Color.BEIGE;
    private static Color darkTile  = Color.SADDLEBROWN;
    private static String pieceStyle = "classic";

    /**
     * Set board tile colors
     */
    public static void setTileColors(Color light, Color dark) {
        lightTile = light;
        darkTile  = dark;
    }

    /**
     * Set piece style folder (must match a subdirectory under /images/)
     */
    public static void setPieceStyle(String style) {
        pieceStyle = style.toLowerCase();
    }

    private boolean flipped = false;

    /**
     * @param gridPane  your GridPane fx:id
     * @param clickHandler  method reference for clicks
     */
    public BoardView(GridPane gridPane, ClickHandler clickHandler) {
        this.grid    = gridPane;
        this.handler = clickHandler;
    }

    /**
     * Draws the entire board from the given model state.
     * @param boardState  8×8 array of Piece or null
     */
    public void update(Piece[][] boardState) {
        grid.getChildren().clear();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                // map logical → visual when flipped
                int row = flipped ? 7 - r : r;
                int col = flipped ? 7 - c : c;

                // create cell
                StackPane cell = new StackPane();
                cell.setPrefSize(tileSize, tileSize);

                // background color
                Color bgColor = ((row + col) % 2 == 0) ? lightTile : darkTile;
                Rectangle bg = new Rectangle(tileSize, tileSize, bgColor);
                cell.getChildren().add(bg);

                // piece image
                Piece p = boardState[row][col];
                if (p != null) {
                    String cls   = p.getClass().getSimpleName().toLowerCase();
                    String color = p.isWhite() ? "white" : "black";
                    String path  = String.format("/images/%s/%s_%s.png",
                            pieceStyle, color, cls);
                    Image imgSrc = new Image(getClass().getResourceAsStream(path));
                    ImageView img = new ImageView(imgSrc);
                    img.setFitWidth(tileSize * 0.8);
                    img.setFitHeight(tileSize * 0.8);
                    cell.getChildren().add(img);
                }

                // attach click
                int rr = row, cc = col;
                cell.setOnMouseClicked(e -> handler.onClick(rr, cc, cell));

                // place in grid
                grid.add(cell, c, r);
            }
        }
    }

    /**
     * Toggle orientation (white-at-bottom vs. black-at-bottom).
     * Call update(...) again to redraw if needed.
     */
    public void flip() {
        flipped = !flipped;
    }

    /** Reset to default orientation */
    public void reset() {
        flipped = false;
    }
}
