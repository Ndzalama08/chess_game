package chess.util;

import chess.model.Piece;

class BoardState {
    final Piece[][] board;
    BoardState(Piece[][] b) {
        board = new Piece[8][8];
        for (int r=0; r<8; r++)
            for (int c=0; c<8; c++)
                board[r][c] = b[r][c];  // shallow copy is fine if you don't modify Piece fields
    }
    static BoardState capture(Piece[][] b) {
        return new BoardState(b);
    }
    void move(int fr, int fc, int tr, int tc) {
        board[tr][tc] = board[fr][fc];
        board[fr][fc] = null;
    }
}
