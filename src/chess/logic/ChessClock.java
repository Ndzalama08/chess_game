package chess.logic;

/**
 * The two players' countdowns. Kept out of GameController so the bookkeeping
 * that decides who ran out of time can be tested without a JavaFX toolkit:
 * the controller previously announced the wrong winner on time and nothing
 * could catch it.
 */
public class ChessClock {
    private int whiteSeconds;
    private int blackSeconds;

    public ChessClock(int minutesPerPlayer) {
        int seconds = minutesPerPlayer * 60;
        this.whiteSeconds = seconds;
        this.blackSeconds = seconds;
    }

    /**
     * Charges one second to the side that is to move.
     *
     * @return true when that side has just run out of time, i.e. the side
     *         passed in is the one that loses.
     */
    public boolean tick(boolean whiteToMove) {
        if (whiteToMove) {
            whiteSeconds = Math.max(0, whiteSeconds - 1);
            return whiteSeconds == 0;
        }
        blackSeconds = Math.max(0, blackSeconds - 1);
        return blackSeconds == 0;
    }

    public int getWhiteSeconds() {
        return whiteSeconds;
    }

    public int getBlackSeconds() {
        return blackSeconds;
    }

    /** "mm:ss" for display. */
    public static String format(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    /** The side that wins when {@code whiteExpired}'s clock reaches zero. */
    public static String winnerOnTime(boolean whiteExpired) {
        return whiteExpired ? "Black" : "White";
    }
}
