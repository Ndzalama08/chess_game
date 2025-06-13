package chess.model;

import chess.model.pieces.*;

public class Board {
    private Piece[][] board;

    public Board() {
        board = new Piece[8][8];
        initializeBoard(board);
    }

    public Board(Piece[][] src) {
        for (int r = 0; r < 8; r++) {
            System.arraycopy(src[r], 0, board[r], 0, 8);
        }
    }
    public Piece[][] getBoard() {
        return board;
    }


    public void initializeBoard(Piece[][] board) {
        // Pawns
        for (int i = 0; i < 8; i++) {

            board[1][i] = new Pawn(false);
            board[6][i] = new Pawn(true);
        }

        // Rooks
        board[0][0] = new Rook(false);
        board[0][7] = new Rook(false);
        board[7][0] = new Rook(true);
        board[7][7] = new Rook(true);

        // Knights
        board[0][1] = new Knight(false);
        board[0][6] = new Knight(false);
        board[7][1] = new Knight(true);
        board[7][6] = new Knight(true);

        // Bishops
        board[0][2] = new Bishop(false);
        board[0][5] = new Bishop(false);
        board[7][2] = new Bishop(true);
        board[7][5] = new Bishop(true);

        // Queens
        board[0][3] = new Queen(false);
        board[7][3] = new Queen(true);

        // Kings
        board[0][4] = new King(false);
        board[7][4] = new King(true);
    }


    public String[][] exportToIds() {
        String[][] ids = new String[8][8];
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p == null) {
                    ids[r][c] = null;
                } else {
                    String color = p.isWhite() ? "w" : "b";
                    String cls   = p.getClass().getSimpleName().toLowerCase();
                    ids[r][c] = color + "_" + cls;
                }
            }
        }
        return ids;
    }

    /**
     * Reconstruct a fresh Board from the given ID grid.
     * All other squares start empty.
     */
    public static Board importFromIds(String[][] ids) {
        Board b = new Board();           // starts empty or default
        // Clear any default pieces if your ctor fills them:
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                b.board[r][c] = null;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                String id = ids[r][c];
                if (id == null) continue;

                boolean isWhite = id.charAt(0) == 'w';
                String type = id.substring(2);  // after "w_" or "b_"
                Piece p;

                switch (type) {
                    case "pawn":   p = new Pawn(isWhite);   break;
                    case "rook":   p = new Rook(isWhite);   break;
                    case "knight": p = new Knight(isWhite); break;
                    case "bishop": p = new Bishop(isWhite); break;
                    case "queen":  p = new Queen(isWhite);  break;
                    case "king":   p = new King(isWhite);   break;
                    default:       continue;               // skip unrecognized
                }
                p.setPosition(r, c);
                b.board[r][c] = p;
            }
        }
        return b;
    }

}

