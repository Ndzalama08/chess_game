package chess.util;

import chess.model.Move;
import chess.model.Piece;
import chess.model.pieces.King;
import chess.model.pieces.Pawn;
import chess.model.pieces.Rook;

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

    /**
     * Check castling conditions (no moved king/rook, path clear, no checks).
     */
    public static boolean canCastle(Move m, Piece[][] board, boolean isWhite) {
        Piece p = board[m.fromRow][m.fromCol];
        if (!(p instanceof King) || p.hasMoved()) return false;
        int dir = (m.toCol > m.fromCol) ? 1 : -1;
        int rookCol = (dir > 0) ? 7 : 0;
        Piece rook = board[m.fromRow][rookCol];
        if (!(rook instanceof Rook) || rook.hasMoved()) return false;
        // path clear
        for (int c = m.fromCol + dir; c != rookCol; c += dir) {
            if (board[m.fromRow][c] != null) return false;
        }

        return true;
    }

    /**
     * True if move matches an en passant pattern: pawn diagonal onto empty.
     */
    public static boolean isEnPassant(Move m, Piece[][] board) {
        Piece p = board[m.fromRow][m.fromCol];
        if (!(p instanceof Pawn)) return false;
        int dy = m.toRow - m.fromRow;
        int dx = m.toCol - m.fromCol;
        if (Math.abs(dx) != 1) return false;
        if (p.isWhite() && dy != -1) return false;
        if (!p.isWhite() && dy != 1) return false;
        // target square must be empty
        return board[m.toRow][m.toCol] == null;
    }

    /**
     * Check en passant legality: last move must be adjacent pawn two-step.
     */
    public static boolean canEnPassant(Move m, Piece[][] board, Move lastMove) {
        if (lastMove == null) return false;
        Piece p = board[lastMove.toRow][lastMove.toCol];
        if (!(p instanceof Pawn)) return false;
        // last move two-step
        if (Math.abs(lastMove.toRow - lastMove.fromRow) != 2) return false;
        // the last pawn must be adjacent to our pawn
        if (lastMove.toRow != m.fromRow) return false;
        if (lastMove.toCol != m.toCol) return false;
        return true;
    }

    public static boolean isPromotion(Move m, Piece[][] board) {
        Piece p = board[m.fromRow][m.fromCol];
        if (!(p instanceof Pawn)) return false;
        return (p.isWhite() && m.toRow == 0)
                || (!p.isWhite() && m.toRow == board.length - 1);
    }
}

