package chess.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.Objects;

public abstract class Piece {
    protected boolean isWhite;
    protected ImageView pieceImage;
    protected int row;
    protected int col;
    private boolean hasMoved = false;

    protected Piece(boolean isWhite, String imagePath) {
        this.isWhite = isWhite;

        InputStream stream = getClass().getResourceAsStream(imagePath);
        if (stream == null) {
            throw new IllegalArgumentException("Image not found: " + imagePath);
        }

        this.pieceImage = new ImageView(new Image(stream));
        this.pieceImage.setFitWidth(60);
        this.pieceImage.setFitHeight(60);
    }

    public Piece(boolean isWhite) {
        // stub, not used
        this(isWhite, "");
    }

    public boolean isWhite() {
        return isWhite;
    }

    public ImageView getPieceImage() {
        return pieceImage;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    // Returns true if the piece has left it's starting square
    public boolean hasMoved() {
        return hasMoved;
    }

    //called after GameManager successfully moves a piece
    public void markMoved() {
        hasMoved = true;
    }

    public abstract boolean isValidMove(Move move, Piece[][] board);

}
