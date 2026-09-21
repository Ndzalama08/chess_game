package chess.logic;

import chess.model.Board;
import chess.model.Move;
import chess.model.Piece;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * generateLegalMoves must answer for the side it is asked about, not just for
 * whoever happens to be to move, and must leave the turn as it found it.
 */
class LegalMoveGenerationTest {

    @Test
    void generatesOpeningMovesForTheSideToMove() {
        GameManager gm = new GameManager(new Board());
        assertEquals(20, gm.generateLegalMoves(true).size(),
                "white has 20 legal opening moves");
    }

    @Test
    void generatesMovesForTheSideNotToMove() {
        GameManager gm = new GameManager(new Board()); // white to move
        List<Move> blackMoves = gm.generateLegalMoves(false);
        assertEquals(20, blackMoves.size(),
                "black also has 20 opening moves, even when it is white's turn");
    }

    @Test
    void generatingMovesDoesNotChangeWhoseTurnItIs() {
        GameManager gm = new GameManager(new Board());
        gm.generateLegalMoves(false);
        assertTrue(gm.isWhiteTurn(), "turn must be restored after generation");
    }

    @Test
    void isInCheckIsFalseWhenTheSideHasNoKing() {
        Board board = new Board();
        Piece[][] arr = board.getBoard();
        arr[7][4] = null; // remove the white king
        GameManager gm = new GameManager(board);

        assertDoesNotThrow(() -> gm.isInCheck(true));
        assertFalse(gm.isInCheck(true));
    }
}
