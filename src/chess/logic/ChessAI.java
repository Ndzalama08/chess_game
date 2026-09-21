package chess.logic;

import chess.model.Board;
import chess.model.Move;
import chess.model.Piece;

import java.util.List;

public class ChessAI {
    /** Score assigned to a mated position; far beyond any material swing. */
    private static final int MATE_SCORE = 1_000_000;

    private final int maxDepth;

    public ChessAI(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /** Entry point: choose the best move for the current side to move */
    public Move findBestMove(GameManager gm) {
        // evaluateBoard is white-positive, so white maximises and black minimises.
        boolean maximizing = gm.isWhiteTurn();
        Move best = null;
        int bestScore = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move m : gm.generateLegalMoves(gm.isWhiteTurn())) {
            GameManager sim = gm.clone();       // deep-copy manager+board
            sim.attemptMove(m);
            int score = minimax(sim, maxDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (best == null || (maximizing ? score > bestScore : score < bestScore)) {
                bestScore = score;
                best = m;
            }
        }
        return best;
    }

    /**
     * Whether this node maximises is decided by whose turn it is in gm, not by
     * a flag threaded through the recursion: the two used to disagree, which
     * made the AI play the opponent's best move when it had the black pieces.
     */
    private int minimax(GameManager gm, int depth, int alpha, int beta) {
        boolean maximizing = gm.isWhiteTurn();

        if (gm.isCheckmate()) {
            // The side to move is mated, so this is a win for the other side.
            return maximizing ? -MATE_SCORE : MATE_SCORE;
        }
        List<Move> moves = gm.generateLegalMoves(maximizing);
        if (depth == 0 || moves.isEmpty()) {
            return evaluateBoard(gm.getBoardWrapper());
        }

        int bestEval = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Move m : moves) {
            GameManager sim = gm.clone();
            sim.attemptMove(m);
            int eval = minimax(sim, depth - 1, alpha, beta);
            if (maximizing) {
                bestEval = Math.max(bestEval, eval);
                alpha = Math.max(alpha, eval);
            } else {
                bestEval = Math.min(bestEval, eval);
                beta = Math.min(beta, eval);
            }
            if (beta <= alpha) break;
        }
        return bestEval;
    }

    /** Simple material-count evaluation: White positive, Black negative */
    private int evaluateBoard(Board board) {
        int score = 0;
        for (Piece[] row : board.getBoard()) {
            for (Piece p : row) {
                if (p != null) {
                    int v = switch (p.getClass().getSimpleName()) {
                        case "Pawn"   -> 100;
                        case "Knight","Bishop" -> 300;
                        case "Rook"   -> 500;
                        case "Queen"  -> 900;
                        default       -> 0;
                    };
                    score += p.isWhite() ? v : -v;
                }
            }
        }
        return score;
    }
}
