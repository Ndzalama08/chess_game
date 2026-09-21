package chess.model;

import chess.logic.GameManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Copy-constructor tests: a copied board must share nothing mutable with the
 * original, otherwise AI search corrupts the real game's castling rights.
 */
class BoardTest {

    @Test
    void copyConstructorCopiesPiecesNotReferences() {
        Board original = new Board();
        Board copy = new Board(original.getBoard());

        assertNotSame(original.getBoard()[7][4], copy.getBoard()[7][4],
                "copied square should hold a distinct Piece instance");
        assertInstanceOf(chess.model.pieces.King.class, copy.getBoard()[7][4]);
    }

    @Test
    void copyPreservesHasMovedFlag() {
        Board original = new Board();
        original.getBoard()[7][0].markMoved();

        Board copy = new Board(original.getBoard());

        assertTrue(copy.getBoard()[7][0].hasMoved(), "hasMoved must survive the copy");
    }

    @Test
    void movingInAClonedGameLeavesTheOriginalUntouched() {
        GameManager real = new GameManager(new Board());
        Piece[][] realArr = real.getBoardWrapper().getBoard();
        Piece whiteKing = realArr[7][4];
        Piece whiteKingsideRook = realArr[7][7];

        GameManager sim = real.clone();
        Piece[][] simArr = sim.getBoardWrapper().getBoard();
        simArr[7][5] = null; // clear f1
        simArr[7][6] = null; // clear g1
        assertTrue(sim.attemptMove(new Move(7, 4, 7, 6)), "clone should be able to castle");

        assertFalse(whiteKing.hasMoved(),
                "castling in the clone must not mark the real king as moved");
        assertFalse(whiteKingsideRook.hasMoved(),
                "castling in the clone must not mark the real rook as moved");
        assertNotNull(realArr[7][5], "real bishop on f1 should still be there");
        assertSame(whiteKingsideRook, realArr[7][7], "real rook should not have moved");
    }
}
