package chess.model.pieces;

import chess.model.Move;
import chess.model.Piece;
import chess.util.MoveValidator;
import chess.view.BoardView;


public class Bishop extends Piece {

    public Bishop(boolean isWhite) {
        super(isWhite,String.format("/images/%s/%s_rook.png", BoardView.getPieceStyle(),
                        isWhite ? "white" : "black"));
    }

    @Override
    public boolean isValidMove(Move move, Piece[][] board) {
        return chess.util.MoveValidator.isDiagonalMove(move, board, isWhite);
    }

}
