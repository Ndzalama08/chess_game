package chess.logic;

import chess.model.Board;
import chess.model.Move;
import chess.model.Piece;
import chess.model.pieces.King;
import chess.model.pieces.Queen;
import chess.model.pieces.Rook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The AI must play for whichever side is to move. Material evaluation is
 * white-positive, so a search that always maximises hands black's move to
 * white's benefit.
 */
class ChessAITest {

    /** Empty board with just the two kings, in corners well clear of the action. */
    private static Board bareKings() {
        Board board = new Board();
        Piece[][] arr = board.getBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                arr[r][c] = null;
            }
        }
        place(arr, 0, 0, new King(false)); // black king a8
        place(arr, 7, 7, new King(true));  // white king h1
        return board;
    }

    private static void place(Piece[][] arr, int row, int col, Piece p) {
        p.setPosition(row, col);
        p.markMoved(); // keep castling out of these positions
        arr[row][col] = p;
    }

    @Test
    void blackTakesTheHangingQueen() {
        Board board = bareKings();
        Piece[][] arr = board.getBoard();
        place(arr, 4, 3, new Queen(true)); // white queen d4, undefended
        place(arr, 0, 3, new Rook(false)); // black rook d8

        GameManager gm = new GameManager(board);
        gm.setWhiteTurn(false);

        Move best = new ChessAI(2).findBestMove(gm);

        assertNotNull(best, "black must find a move");
        assertEquals(4, best.toRow, "black should capture on d4");
        assertEquals(3, best.toCol, "black should capture on d4");
    }

    @Test
    void whiteTakesTheHangingQueen() {
        Board board = bareKings();
        Piece[][] arr = board.getBoard();
        place(arr, 4, 3, new Queen(false)); // black queen d4, undefended
        place(arr, 7, 3, new Rook(true));   // white rook d1

        GameManager gm = new GameManager(board);
        gm.setWhiteTurn(true);

        Move best = new ChessAI(2).findBestMove(gm);

        assertNotNull(best, "white must find a move");
        assertEquals(4, best.toRow, "white should capture on d4");
        assertEquals(3, best.toCol, "white should capture on d4");
    }

    @Test
    void searchLeavesTheRealGameUntouched() {
        GameManager gm = new GameManager(new Board());
        Piece whiteKing = gm.getBoardWrapper().getBoard()[7][4];

        new ChessAI(2).findBestMove(gm);

        assertTrue(gm.isWhiteTurn(), "searching must not consume the turn");
        assertFalse(whiteKing.hasMoved(), "searching must not move the real pieces");
    }
}
