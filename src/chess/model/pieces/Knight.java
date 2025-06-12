package chess.model.pieces;

import chess.model.Move;
import chess.model.Piece;

public class Knight extends Piece {

    public Knight(boolean isWhite, String imagePath) {
        super(isWhite, imagePath);
    }

    @Override
    public boolean isValidMove(Move move, Piece[][] board) {
        int dx = Math.abs(move.toCol - move.fromCol);
        int dy = Math.abs(move.toRow - move.fromRow);
        Piece target = board[move.toRow][move.toCol];

        if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
            return target == null || target.isWhite() != isWhite;
        }

        return false;
    }

}
