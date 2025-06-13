package chess.logic;

import chess.model.Board;
import chess.model.Move;
import chess.model.Piece;
import chess.logic.GameManager;
import java.util.List;

public class ChessAI {
    private final int maxDepth;

    public ChessAI(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /** Entry point: choose the best move for the current side to move */
    public Move findBestMove(GameManager gm) {
        return minimaxRoot(gm, maxDepth);
    }

    private Move minimaxRoot(GameManager gm, int depth) {
        Move best = null;
        int bestScore = Integer.MIN_VALUE;
        for (Move m : gm.generateLegalMoves(gm.isWhiteTurn())) {
            GameManager sim = gm.clone();       // deep‐copy manager+board
            sim.attemptMove(m);
            int score = minimax(sim, depth - 1,
                    Integer.MIN_VALUE, Integer.MAX_VALUE,
                    !gm.isWhiteTurn());
            if (score > bestScore) {
                bestScore = score;
                best = m;
            }
        }
        return best;
    }

    private int minimax(GameManager gm, int depth,
                        int alpha, int beta,
                        boolean maximizingPlayer) {
        if (depth == 0 || gm.isCheckmate()) {
            return evaluateBoard(gm.getBoardWrapper());
        }
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move m : gm.generateLegalMoves(gm.isWhiteTurn())) {
                GameManager sim = gm.clone();
                sim.attemptMove(m);
                int eval = minimax(sim, depth - 1,
                        alpha, beta,
                        !maximizingPlayer);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move m : gm.generateLegalMoves(gm.isWhiteTurn())) {
                GameManager sim = gm.clone();
                sim.attemptMove(m);
                int eval = minimax(sim, depth - 1,
                        alpha, beta,
                        !maximizingPlayer);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
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
