package chess.util;

import chess.model.Move;
import chess.model.Piece;

/**
 * Utility class for validating straight-line and diagonal chess moves.
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
}

