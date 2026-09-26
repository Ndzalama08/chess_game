package chess.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The clock decides who loses on time. GameController used to announce the
 * expired player as the winner, so these cover both directions explicitly.
 */
class ChessClockTest {

    @Test
    void bothSidesStartWithTheConfiguredTime() {
        ChessClock clock = new ChessClock(10);

        assertEquals(600, clock.getWhiteSeconds());
        assertEquals(600, clock.getBlackSeconds());
    }

    @Test
    void tickOnlyChargesTheSideToMove() {
        ChessClock clock = new ChessClock(1);

        clock.tick(true);

        assertEquals(59, clock.getWhiteSeconds(), "white was to move");
        assertEquals(60, clock.getBlackSeconds(), "black's clock must not run");
    }

    @Test
    void aClockWithNoTimeFlagsOnTheFirstTick() {
        assertTrue(new ChessClock(0).tick(true));
        assertTrue(new ChessClock(0).tick(false));
    }

    @Test
    void flagFallsOnlyWhenTheSideToMoveRunsOut() {
        ChessClock clock = new ChessClock(1);
        for (int i = 0; i < 59; i++) {
            assertFalse(clock.tick(true), "still has time after " + (i + 1) + " ticks");
        }

        assertTrue(clock.tick(true), "60th tick empties white's clock");
        assertEquals(0, clock.getWhiteSeconds());
        assertEquals(60, clock.getBlackSeconds(), "black never moved, so black never ticked");
    }

    @Test
    void clocksDoNotGoNegative() {
        ChessClock clock = new ChessClock(0);
        clock.tick(true);
        clock.tick(true);

        assertEquals(0, clock.getWhiteSeconds());
    }

    @Test
    void theSideWhoseClockRanOutLoses() {
        assertEquals("Black", ChessClock.winnerOnTime(true), "white ran out, so black wins");
        assertEquals("White", ChessClock.winnerOnTime(false), "black ran out, so white wins");
    }

    @Test
    void formatsAsMinutesAndSeconds() {
        assertEquals("10:00", ChessClock.format(600));
        assertEquals("01:05", ChessClock.format(65));
        assertEquals("00:00", ChessClock.format(0));
    }
}
