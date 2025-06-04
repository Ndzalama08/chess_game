package chess.model.pieces;

import chess.model.Move;
import chess.model.Piece;

public class Queen extends Piece {

    public Queen(boolean isWhite, String imagePath) {
        super(isWhite, imagePath);
    }

    @Override
    public boolean isValidMove(Move move, Piece[][] board) {
        Bishop tempBishop = new Bishop(isWhite,"");
        Rook tempRook = new Rook(isWhite,"");
        return tempBishop.isValidMove(move, board) || tempRook.isValidMove(move, board);
    }

}
