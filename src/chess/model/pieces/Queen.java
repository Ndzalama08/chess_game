// Queen.java
package chess.model.pieces;

import chess.view.BoardView;
import chess.model.Move;
import chess.model.Piece;

public class Queen extends Piece {
    public Queen(boolean isWhite) {
        super(isWhite,String.format("/images/%s/%s_rook.png", BoardView.getPieceStyle(),
                isWhite ? "white" : "black"));
    }

    @Override
    public boolean isValidMove(Move move, Piece[][] board) {
        // Valid if straight or diagonal
        return chess.util.MoveValidator.isStraightLineMove(move, board, isWhite)
                || chess.util.MoveValidator.isDiagonalMove(move, board, isWhite);
    }
}
