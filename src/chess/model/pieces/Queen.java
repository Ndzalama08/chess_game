package chess.model.pieces;

import chess.model.Move;
import chess.model.Piece;

public class Queen extends Piece {

    public Queen(boolean isWhite, String imagePath) {
        super(isWhite, imagePath);
    }

    public boolean isValidMove(Move move, Piece[][] board) {
        return false;
    }
}
