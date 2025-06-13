package chess.model.pieces;

import chess.view.BoardView;
import chess.model.Move;
import chess.model.Piece;

public class Rook extends Piece {
    public Rook(boolean isWhite) {
        super(isWhite, String.format("/images/%s/%s_rook.png", BoardView.getPieceStyle(),
                        isWhite ? "white" : "black"));
    }

    @Override
    public boolean isValidMove(Move move, Piece[][] board) {
        return chess.util.MoveValidator.isStraightLineMove(move, board, isWhite);
    }
}

