package chess.model.pieces;

import chess.model.Move;
import chess.model.Piece;

public class King extends Piece {

    public King(boolean isWhite, String imagePath) {
        super(isWhite, imagePath);
    }

    @Override
    public boolean isValidMove(Move move, Piece[][] board) {
        int dx = Math.abs(move.toCol - move.fromCol);
        int dy = Math.abs(move.toRow - move.fromRow);
        Piece target = board[move.toRow][move.toCol];

        if (dx <= 1 && dy <= 1) {
            return target == null || target.isWhite() != isWhite;
        }

        return false;
    }


}
