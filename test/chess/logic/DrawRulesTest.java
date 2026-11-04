package chess.logic;

import chess.model.Board;
import chess.model.Move;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DrawRulesTest {

    private static boolean play(GameManager gm, String from, String to) {
        return gm.attemptMove(new Move(8 - (from.charAt(1) - '0'), from.charAt(0) - 'a',
                8 - (to.charAt(1) - '0'), to.charAt(0) - 'a'));
    }

    @Test
    void bareKingsAreDrawn() {
        assertTrue(PerftTest.fromFen("8/8/4k3/8/8/3K4/8/8 w - - 0 1").isDrawByInsufficientMaterial());
    }

    @Test
    void kingAndOneMinorAreDrawn() {
        assertTrue(PerftTest.fromFen("8/8/4k3/8/8/3KN3/8/8 w - - 0 1").isDrawByInsufficientMaterial());
        assertTrue(PerftTest.fromFen("8/8/4k3/8/8/3KB3/8/8 w - - 0 1").isDrawByInsufficientMaterial());
    }

    @Test
    void sameColourBishopsAreDrawnOppositeColourAreNot() {
        // c1 and f4 are both dark squares; c1 and d1 differ
        assertTrue(PerftTest.fromFen("8/8/4k3/8/5b2/3K4/8/2B5 w - - 0 1").isDrawByInsufficientMaterial());
        assertFalse(PerftTest.fromFen("8/8/4k3/8/8/3K4/8/2BB4 w - - 0 1").isDrawByInsufficientMaterial());
    }

    @Test
    void aPawnRookOrQueenIsEnoughMaterial() {
        assertFalse(PerftTest.fromFen("8/8/4k3/8/8/3K4/4P3/8 w - - 0 1").isDrawByInsufficientMaterial());
        assertFalse(PerftTest.fromFen("8/8/4k3/8/8/3K4/8/7R w - - 0 1").isDrawByInsufficientMaterial());
        assertFalse(new GameManager(new Board()).isDrawByInsufficientMaterial());
    }

    @Test
    void fiftyMoveRuleTriggersAtOneHundredHalfMoves() {
        GameManager gm = PerftTest.fromFen("8/8/4k3/8/8/3K4/8/7R w - - 0 1");
        gm.halfmoveClock = 98;
        assertFalse(gm.isDrawByFiftyMoves());
        assertTrue(play(gm, "h1", "h2"));
        assertTrue(play(gm, "e6", "e7"));
        assertTrue(gm.isDrawByFiftyMoves());
        assertEquals("the fifty-move rule", gm.drawReason());
    }

    @Test
    void pawnMoveResetsTheFiftyMoveClock() {
        GameManager gm = new GameManager(new Board());
        gm.halfmoveClock = 90;
        assertTrue(play(gm, "e2", "e4"));
        assertEquals(0, gm.halfmoveClock);
    }

    @Test
    void threefoldRepetition() {
        GameManager gm = new GameManager(new Board());
        for (int i = 0; i < 2; i++) {
            assertNull(gm.drawReason());
            assertTrue(play(gm, "g1", "f3"));
            assertTrue(play(gm, "g8", "f6"));
            assertTrue(play(gm, "f3", "g1"));
            assertTrue(play(gm, "f6", "g8"));
        }
        assertEquals("threefold repetition", gm.drawReason());
    }

    @Test
    void repetitionHistorySurvivesCloning() {
        GameManager gm = new GameManager(new Board());
        for (int i = 0; i < 2; i++) {
            play(gm, "g1", "f3"); play(gm, "g8", "f6");
            play(gm, "f3", "g1");
            if (i == 0) play(gm, "f6", "g8");
        }
        GameManager copy = gm.clone();
        assertTrue(play(copy, "f6", "g8"));
        assertTrue(copy.isDrawByRepetition());
        assertFalse(gm.isDrawByRepetition(), "original must not see the copy's move");
    }
}
