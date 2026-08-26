package chess.model.pieces;

import chess.model.Move;
import chess.model.Piece;

public class Rook extends Piece {
    public Rook(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(Move move, Piece[][] board) {
        return chess.util.MoveValidator.isStraightLineMove(move, board, isWhite);
    }
}

