package chess.logic;

import chess.model.Board;
import chess.model.Move;
import chess.model.Piece;
import chess.model.pieces.King;
import chess.model.pieces.Queen;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Checkmate vs stalemate, and the special moves that count as an escape. */
class GameEndTest {

    private static Board emptyBoard() {
        Board board = new Board();
        Piece[][] arr = board.getBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                arr[r][c] = null;
            }
        }
        return board;
    }

    private static void place(Piece[][] arr, int row, int col, Piece p) {
        p.setPosition(row, col);
        p.markMoved();
        arr[row][col] = p;
    }

    @Test
    void blackKingWithNoMovesAndNoCheckIsStalemate() {
        Board board = emptyBoard();
        Piece[][] arr = board.getBoard();
        place(arr, 0, 0, new King(false));  // black king a8
        place(arr, 2, 1, new Queen(true));  // white queen b6 covers a7, b7, b8
        place(arr, 7, 7, new King(true));   // white king h1

        GameManager gm = new GameManager(board);
        gm.setWhiteTurn(false);

        assertFalse(gm.isInCheck(false), "black king is not attacked");
        assertFalse(gm.hasLegalMoves(false), "black has nowhere to go");
        assertTrue(gm.isStalemate());
        assertFalse(gm.isCheckmate(), "stalemate is a draw, not a mate");
    }

    @Test
    void backRankMateIsCheckmateNotStalemate() {
        Board board = emptyBoard();
        Piece[][] arr = board.getBoard();
        place(arr, 0, 0, new King(false));  // black king a8
        place(arr, 1, 1, new Queen(true));  // white queen b7, supported by its king
        place(arr, 2, 2, new King(true));   // white king c6

        GameManager gm = new GameManager(board);
        gm.setWhiteTurn(false);

        assertTrue(gm.isInCheck(false));
        assertTrue(gm.isCheckmate());
        assertFalse(gm.isStalemate());
    }

    @Test
    void castlingCountsAsALegalMove() {
        Board board = new Board();
        Piece[][] arr = board.getBoard();
        arr[7][5] = null; // clear bishop f1
        arr[7][6] = null; // clear knight g1

        GameManager gm = new GameManager(board);
        List<Move> moves = gm.generateLegalMoves(true);

        assertTrue(moves.stream().anyMatch(m ->
                        m.fromRow == 7 && m.fromCol == 4 && m.toRow == 7 && m.toCol == 6),
                "O-O should be listed among white's legal moves");
        assertTrue(gm.hasLegalMoves(true));
    }

    @Test
    void openingPositionIsNeitherMateNorStalemate() {
        GameManager gm = new GameManager(new Board());
        assertFalse(gm.isCheckmate());
        assertFalse(gm.isStalemate());
    }
}
