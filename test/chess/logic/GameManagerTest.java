package chess.logic;

import chess.model.Board;
import chess.model.Move;
import chess.model.Piece;
import chess.model.pieces.Pawn;
import chess.model.pieces.Queen;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests for the Phase 0 fixes: the isValid rename, Board's copy
 * constructor NPE, and the castling/en passant ordering bug.
 */
class GameManagerTest {

    @Test
    void cloneDoesNotThrow() {
        GameManager gm = new GameManager(new Board());
        assertDoesNotThrow(gm::clone);
    }

    @Test
    void normalOpeningMovesAreLegal() {
        GameManager gm = new GameManager(new Board());
        assertTrue(gm.attemptMove(new Move(6, 4, 4, 4))); // e2-e4
        assertTrue(gm.attemptMove(new Move(1, 4, 3, 4))); // e7-e5
    }

    @Test
    void kingsideCastlingSucceedsOnceOwnMoveShapeIsBypassed() {
        Board board = new Board();
        Piece[][] arr = board.getBoard();
        arr[7][5] = null; // clear bishop f1
        arr[7][6] = null; // clear knight g1
        GameManager gm = new GameManager(board);

        assertTrue(gm.attemptMove(new Move(7, 4, 7, 6))); // O-O
        assertNotNull(arr[7][6], "king should have moved to g1");
        assertNotNull(arr[7][5], "rook should have moved to f1");
    }

    @Test
    void enPassantCaptureSucceeds() {
        Board board = new Board();
        Piece[][] arr = board.getBoard();
        GameManager gm = new GameManager(board);

        assertTrue(gm.attemptMove(new Move(6, 4, 4, 4))); // e2-e4
        assertTrue(gm.attemptMove(new Move(1, 0, 2, 0))); // a7-a6 (filler)
        assertTrue(gm.attemptMove(new Move(4, 4, 3, 4))); // e4-e5
        assertTrue(gm.attemptMove(new Move(1, 3, 3, 3))); // d7-d5

        assertTrue(gm.attemptMove(new Move(3, 4, 2, 3))); // exd6 en passant
        assertNull(arr[3][3], "captured black pawn should be removed");
    }

    @Test
    void promotionDefaultsToQueenWithNoChooserSet() {
        Board board = new Board();
        Piece[][] arr = board.getBoard();
        arr[1][0] = new Pawn(true);
        arr[1][0].setPosition(1, 0);
        arr[0][0] = null; // clear so the pawn has somewhere to promote into
        GameManager gm = new GameManager(board);

        assertTrue(gm.attemptMove(new Move(1, 0, 0, 0)));
        assertInstanceOf(Queen.class, arr[0][0], "should auto-promote to Queen headlessly");
    }
}
