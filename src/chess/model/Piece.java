package chess.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Piece {
    protected boolean isWhite;
    protected ImageView pieceImage;
    protected int row;
    protected int col;

    public Piece(boolean isWhite, String imagePath) {
        this.isWhite = isWhite;
        this.pieceImage = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        this.pieceImage.setFitWidth(60);
        this.pieceImage.setFitHeight(60);
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

    public abstract boolean isValidMove(Move move, Piece[][] board);

}
