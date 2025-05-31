package chess.model;

import chess.model.pieces.Pawn;

public class Board {
    private Piece[][] board;

    public Board() {
        board = new Piece[8][8];
        initializeBoard();
    }

    private void initializeBoard() {

    }
}
