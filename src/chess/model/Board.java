package chess.model;

import chess.model.pieces.*;

public class Board {
    private Piece[][] board;

    public Board() {
        board = new Piece[8][8];
        initializeBoard(board);
    }

    public void initializeBoard(Piece[][] board) {
        // Pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(false, "/images/black_pawn.png");
            board[6][i] = new Pawn(true, "/images/white_pawn.png");
        }

        // Rooks
        board[0][0] = new Rook(false, "/images/black_rook.png");
        board[0][7] = new Rook(false, "/images/black_rook.png");
        board[7][0] = new Rook(true, "/images/white_rook.png");
        board[7][7] = new Rook(true, "/images/white_rook.png");

        // Knights
        board[0][1] = new Knight(false, "/images/black_knight.png");
        board[0][6] = new Knight(false, "/images/black_knight.png");
        board[7][1] = new Knight(true, "/images/white_knight.png");
        board[7][6] = new Knight(true, "/images/white_knight.png");

        // Bishops
        board[0][2] = new Bishop(false, "/images/black_bishop.png");
        board[0][5] = new Bishop(false, "/images/black_bishop.png");
        board[7][2] = new Bishop(true, "/images/white_bishop.png");
        board[7][5] = new Bishop(true, "/images/white_bishop.png");

        // Queens
        board[0][3] = new Queen(false, "/images/black_queen.png");
        board[7][3] = new Queen(true, "/images/white_queen.png");

        // Kings
        board[0][4] = new King(false, "/images/black_king.png");
        board[7][4] = new King(true, "/images/white_king.png");
    }

}

