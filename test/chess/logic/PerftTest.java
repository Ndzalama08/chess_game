package chess.logic;

import chess.model.Board;
import chess.model.Move;
import chess.model.Piece;
import chess.model.pieces.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Perft: counts leaf nodes of the legal-move tree and compares with published
 * numbers (chessprogramming.org/Perft_Results). Any rule bug in move
 * generation changes the count. Only promotion-free positions/depths are used
 * because the generator offers just the queen promotion.
 */
class PerftTest {

    static long perft(GameManager gm, int depth) {
        if (depth == 0) return 1;
        long nodes = 0;
        for (Move m : gm.generateLegalMoves(gm.isWhiteTurn())) {
            GameManager next = gm.clone();
            next.attemptMove(m);
            nodes += perft(next, depth - 1);
        }
        return nodes;
    }

    /** Minimal FEN reader: pieces, side to move and castling rights only. */
    static GameManager fromFen(String fen) {
        String[] f = fen.split(" ");
        Board board = Board.importFromIds(new String[8][8]);
        Piece[][] b = board.getBoard();
        String[] ranks = f[0].split("/");
        for (int r = 0; r < 8; r++) {
            int c = 0;
            for (char ch : ranks[r].toCharArray()) {
                if (Character.isDigit(ch)) { c += ch - '0'; continue; }
                boolean white = Character.isUpperCase(ch);
                Piece p = switch (Character.toLowerCase(ch)) {
                    case 'p' -> new Pawn(white);
                    case 'n' -> new Knight(white);
                    case 'b' -> new Bishop(white);
                    case 'r' -> new Rook(white);
                    case 'q' -> new Queen(white);
                    default  -> new King(white);
                };
                p.setPosition(r, c);
                b[r][c++] = p;
            }
        }
        // kings and rooks without a castling right count as already moved
        String rights = f[2];
        mark(b[7][4], !rights.contains("K") && !rights.contains("Q"));
        mark(b[7][7], !rights.contains("K"));
        mark(b[7][0], !rights.contains("Q"));
        mark(b[0][4], !rights.contains("k") && !rights.contains("q"));
        mark(b[0][7], !rights.contains("k"));
        mark(b[0][0], !rights.contains("q"));
        GameManager gm = new GameManager(board);
        gm.setWhiteTurn(f[1].equals("w"));
        return gm;
    }

    private static void mark(Piece p, boolean moved) {
        if (p != null && moved) p.markMoved();
    }

    @Test
    void startingPosition() {
        GameManager gm = new GameManager(new Board());
        assertEquals(20, perft(gm, 1));
        assertEquals(400, perft(gm, 2));
        assertEquals(8902, perft(gm, 3));
    }

    /** Castling both ways, pins, en passant, captures everywhere. */
    @Test
    void kiwipete() {
        GameManager gm = fromFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        assertEquals(48, perft(gm, 1));
        assertEquals(2039, perft(gm, 2));
    }

    /** Contains the en passant capture that would expose the king along the rank. */
    @Test
    void enPassantPin() {
        GameManager gm = fromFen("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
        assertEquals(14, perft(gm, 1));
        assertEquals(191, perft(gm, 2));
        assertEquals(2812, perft(gm, 3));
    }
}
