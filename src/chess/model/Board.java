package chess.model;

import chess.model.pieces.*;

public class Board {
    private Piece[][] board;

    public Board() {
        board = new Piece[8][8];
        initializeBoard(board);
    }
    public Piece[][] getBoard() {
        return board;
    }


    public void initializeBoard(Piece[][] board) {
        // Pawns
        for (int i = 0; i < 8; i++) {

            board[1][i] = new Pawn(false, "/pieces/black_pawn.png");
            board[6][i] = new Pawn(true, "/pieces/white_pawn.png");
        }

        // Rooks
        board[0][0] = new Rook(false, "/pieces/black_rook.png");
        board[0][7] = new Rook(false, "/pieces/black_rook.png");
        board[7][0] = new Rook(true, "/pieces/white_rook.png");
        board[7][7] = new Rook(true, "/pieces/white_rook.png");

        // Knights
        board[0][1] = new Knight(false, "/pieces/black_knight.png");
        board[0][6] = new Knight(false, "/pieces/black_knight.png");
        board[7][1] = new Knight(true, "/pieces/white_knight.png");
        board[7][6] = new Knight(true, "/pieces/white_knight.png");

        // Bishops
        board[0][2] = new Bishop(false, "/pieces/black_bishop.png");
        board[0][5] = new Bishop(false, "/pieces/black_bishop.png");
        board[7][2] = new Bishop(true, "/pieces/white_bishop.png");
        board[7][5] = new Bishop(true, "/pieces/white_bishop.png");

        // Queens
        board[0][3] = new Queen(false, "/pieces/black_queen.png");
        board[7][3] = new Queen(true, "/pieces/white_queen.png");

        // Kings
        board[0][4] = new King(false, "/pieces/black_king.png");
        board[7][4] = new King(true, "/pieces/white_king.png");
    }

}

