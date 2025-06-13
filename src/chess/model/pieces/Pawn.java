package chess.model.pieces;

import chess.view.BoardView;
import chess.model.Move;
import chess.model.Piece;

public class Pawn extends Piece {

    public Pawn(boolean isWhite) {
        super(isWhite,String.format("/images/%s/%s_rook.png", BoardView.getPieceStyle(),
                isWhite ? "white" : "black"));
    }

    @Override
    public boolean isValidMove(Move move, Piece[][] board) {
        int dir = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1;

        int rowDiff = move.toRow - move.fromRow;
        int colDiff = Math.abs(move.toCol - move.fromCol);

        // Move forward
        if (colDiff == 0) {
            if (rowDiff == dir && board[move.toRow][move.toCol] == null) return true;
            if (move.fromRow == startRow && rowDiff == 2 * dir && board[move.toRow][move.toCol] == null && board[move.fromRow + dir][move.toCol] == null) return true;
        }

        // Capture
        if (colDiff == 1 && rowDiff == dir && board[move.toRow][move.toCol]
                != null && board[move.toRow][move.toCol].isWhite() != isWhite){
            return true;
        }

        return false;
    }

}
