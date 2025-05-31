package chess.model.pieces;

import chess.model.Move;
import chess.model.Piece;

public class Bishop extends Piece {

    public Bishop(boolean iswhite, String imagePath) {
        super(iswhite, "asdsa");
    }

    @Override
    public boolean isValidMove(Move move, Piece[][] board) {
        return false;
    }
}
