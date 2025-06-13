package chess.util;

import chess.model.Piece;

public class BoardState {
    final Piece[][] board;
    BoardState(Piece[][] b) {
        board = new Piece[8][8];
        for (int r=0; r<8; r++)
            for (int c=0; c<8; c++)
                board[r][c] = b[r][c];  // shallow copy is fine if you don't modify Piece fields
    }

    public Piece[][] getBoard() {
        return board;
    }
    public static BoardState capture(Piece[][] src) {
        return new BoardState(src);
    }

    public void move(int fr, int fc, int tr, int tc) {
        board[tr][tc] = board[fr][fc];
        board[fr][fc] = null;
    }
}
