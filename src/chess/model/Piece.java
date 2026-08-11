package chess.model;

public abstract class Piece {
    protected boolean isWhite;
    protected int row;
    protected int col;
    private boolean hasMoved = false;

    public Piece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public boolean isWhite() {
        return isWhite;
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
