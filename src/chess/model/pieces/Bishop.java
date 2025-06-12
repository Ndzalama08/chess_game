package chess.model.pieces;

import chess.model.Move;
import chess.model.Piece;
import chess.view.BoardView;


public class Bishop extends Piece {

    public Bishop(boolean isWhite) {
        super(isWhite,String.format("/images/%s/%s_rook.png", BoardView.getPieceStyle(),
                        isWhite ? "white" : "black"));
    }

    @Override
    public boolean isValidMove(Move move, Piece[][] board) {
        int dx = move.toCol - move.fromCol;
        int dy = move.toRow - move.fromRow;

        if (Math.abs(dx) != Math.abs(dy)) return false;

        int stepX = Integer.signum(dx);
        int stepY = Integer.signum(dy);

        int x = move.fromCol + stepX;
        int y = move.fromRow + stepY;

        while (x != move.toCol && y != move.toRow) {
            if (board[y][x] != null) return false;
            x += stepX;
            y += stepY;
        }

        Piece target = board[move.toRow][move.toCol];
        return target == null || target.isWhite() != isWhite;
    }

}
