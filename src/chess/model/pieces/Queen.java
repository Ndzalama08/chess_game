// Queen.java
package chess.model.pieces;

import chess.model.Move;
import chess.model.Piece;

public class Queen extends Piece {
    public Queen(boolean isWhite, String imagePath) {
        super(isWhite, imagePath);
    }

    @Override
    public boolean isValidMove(Move move, Piece[][] board) {
        // Valid if straight or diagonal
        return chess.util.MoveValidator.isStraightLineMove(move, board, isWhite)
                || chess.util.MoveValidator.isDiagonalMove(move, board, isWhite);
    }
}
