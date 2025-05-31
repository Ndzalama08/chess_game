package chess.model.pieces;

import chess.model.Move;
import chess.model.Piece;

public class Rook extends Piece {
    public Rook(boolean isWhite,String imagePath) {
        super(isWhite, imagePath);
    }

    @Override
    public boolean isValidMove(Move move, Piece[][] board) {
        return move.startX == move.endX || move.startY == move.endY;
    }
}
