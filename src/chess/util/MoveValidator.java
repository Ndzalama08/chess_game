package chess.util;

import chess.model.Move;
import chess.model.Piece;

/**
 * Utility class for validating straight-line, diagonal or special chess moves.
 */
public final class MoveValidator {
    private MoveValidator() {
        // prevent instantiation
    }

    /**
     * Validates rook-style (horizontal/vertical) movement.
     */
    public static boolean isStraightLineMove(Move move, Piece[][] board, boolean isWhite) {
        int dx = move.toCol - move.fromCol;
        int dy = move.toRow - move.fromRow;
        // Must move in a straight line (either row or column)
        if (dx != 0 && dy != 0) return false;

        int stepX = Integer.signum(dx);
        int stepY = Integer.signum(dy);
        int x = move.fromCol + stepX;
        int y = move.fromRow + stepY;
        // Check for obstructions
        while (x != move.toCol || y != move.toRow) {
            if (board[y][x] != null) return false;
            x += stepX;
            y += stepY;
        }
        // Destination empty or occupied by opponent
        Piece target = board[move.toRow][move.toCol];
        return target == null || target.isWhite() != isWhite;
    }

    /**
     * Validates bishop-style (diagonal) movement.
     */
    public static boolean isDiagonalMove(Move move, Piece[][] board, boolean isWhite) {
        int dx = move.toCol - move.fromCol;
        int dy = move.toRow - move.fromRow;
        // Must move diagonally: absolute dx == absolute dy
        if (Math.abs(dx) != Math.abs(dy)) return false;

        int stepX = Integer.signum(dx);
        int stepY = Integer.signum(dy);
        int x = move.fromCol + stepX;
        int y = move.fromRow + stepY;
        // Check for obstructions
        while (x != move.toCol && y != move.toRow) {
            if (board[y][x] != null) return false;
            x += stepX;
            y += stepY;
        }
        // Destination empty or occupied by opponent
        Piece target = board[move.toRow][move.toCol];
        return target == null || target.isWhite() != isWhite;
    }

    // Special rule checks
    public static boolean isCastling(Move move, Piece[][] board) {
        Piece p = board[move.fromRow][move.fromCol];
        // King moved two squares horizontally, same row
        return p != null && p instanceof chess.model.pieces.King
                && Math.abs(move.toCol - move.fromCol) == 2
                && move.fromRow == move.toRow;
    }

    public static boolean canCastle(Move move, Piece[][] board, boolean isWhite) {
        // Check king/rook haven't moved, path clear, no check through path
        // (Implement your castling permissions here, e.g., flags in Piece or Board)
        // Stub: return true for now
        return true;
    }

    public static boolean isEnPassant(Move move, Piece[][] board) {
        Piece p = board[move.fromRow][move.fromCol];
        // Pawn moves diagonally to empty square behind an opponent pawn
        return p != null && p.getClass().getSimpleName().equals("Pawn")
                && board[move.toRow][move.toCol] == null
                && Math.abs(move.toCol - move.fromCol) == 1
                && move.toRow - move.fromRow == (p.isWhite() ? -1 : 1);
    }

    public static boolean canEnPassant(Move move, Piece[][] board, Move lastMove) {
        // lastMove must be a two-square pawn advance adjacent to this pawn
        // Stub: return true for now
        return true;
    }
}

