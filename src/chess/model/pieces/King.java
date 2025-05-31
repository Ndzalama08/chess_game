package chess.model.pieces;

import chess.model.Move;
import chess.model.Piece;

public class King extends Piece {

    public King(boolean isWhite, String imagePath) {
        super(isWhite, imagePath);
    }

    public boolean isValidMove(Move move, Piece[][] board) {
        return false;
    }

}
