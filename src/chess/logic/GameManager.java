package chess.logic;

import chess.model.Board;
import chess.model.Move;
import chess.model.Piece;
import chess.model.pieces.*;
import chess.util.MoveValidator;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameManager implements Cloneable {
    private final Board board;
    private boolean whiteTurn = true;
    private Move lastMove = null;   // track for en passant

    public GameManager(Board board) {
        this.board = board;
    }

    /** Allow simulations to override whose turn it is. */
    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    /** Optional: expose internal Board if needed by callers. */
    public Board getBoardWrapper() {
        return board;
    }

    /**
     * bundles all pre-move checks
     */
    private boolean basicValidation(Move m) {
        Piece[][] b = board.getBoard();
        Piece p = b[m.fromRow][m.fromCol];

        // 1) Must move your own piece
        if (p == null || p.isWhite() != whiteTurn) return false;

        // 2) Shape/path valid per piece logic
        if (!p.isValidMove(m, b)) return false;

        // 3) Cannot capture own piece
        Piece dest = b[m.toRow][m.toCol];
        if (dest != null && dest.isWhite() == whiteTurn) return false;

        // 4a) Castling
        if (MoveValidator.isCastling(m, b)) {
            if (!MoveValidator.canCastle(m, b, whiteTurn)) return false;
        }
        // 4b) En passant
        if (MoveValidator.isEnPassant(m, b)) {
            if (!MoveValidator.canEnPassant(m, b, lastMove)) return false;
        }
        // 4c) Promotion (optional UI pop-up later)
        if (MoveValidator.isPromotion(m, b)) {  }

        // 5) Must not leave own king in check
        //    simulate the move
        b[m.toRow][m.toCol] = p;
        b[m.fromRow][m.fromCol] = null;
        boolean leavesKingInCheck = isInCheck(whiteTurn);
        // undo
        b[m.fromRow][m.fromCol] = p;
        b[m.toRow][m.toCol] = dest;
        if (leavesKingInCheck) return false;

        return true;
    }

    /**
     * Attempt to move; returns true if valid and performed.
     * Also updates lastMove and flips turn.
     */
    public boolean attemptMove(Move m) {
        if (!basicValidation(m)) {
            return false;
        }

        Piece[][] b = board.getBoard();
        Piece p = b[m.fromRow][m.fromCol];
        Piece dest = b[m.toRow][m.toCol];


        // handle special moves
        if (MoveValidator.isPromotion(m, board.getBoard())) {
            performPromotion(m);
        }
        else if (MoveValidator.isCastling(m, b)) {
            performCastling(m);
        } else if (MoveValidator.isEnPassant(m, b)) {
            performEnPassant(m);
        } else {
            // normal move
            b[m.toRow][m.toCol] = p;
            b[m.fromRow][m.fromCol] = null;
        }

        // record lastMove for en passant checks
        p.markMoved();
        lastMove = m;
        whiteTurn = !whiteTurn;
        return true;
    }

    /**
     * Moves king and rook appropriately when castling
     */
    private void performCastling(Move m) {
        Piece[][] b = board.getBoard();
        Piece king = b[m.fromRow][m.fromCol];
        int dir = Integer.signum(m.toCol - m.fromCol);
        int rookCol = (dir > 0 ? 7 : 0);
        Piece rook = b[m.fromRow][rookCol];

        // move king
        b[m.toRow][m.toCol] = king;
        b[m.fromRow][m.fromCol] = null;

        // move rook next to king
        int newRookCol = m.toCol - dir;
        b[m.fromRow][newRookCol] = rook;
        b[m.fromRow][rookCol] = null;
    }

    private void performPromotion(Move m) {
        Piece[][] b = board.getBoard();
        Pawn pawn = (Pawn) b[m.fromRow][m.fromCol];
        // remove the pawn
        b[m.fromRow][m.fromCol] = null;

        // Ask user which piece:
        ChoiceDialog<String> dlg = new ChoiceDialog<>("Queen",
                List.of("Queen","Rook","Bishop","Knight"));
        dlg.setTitle("Pawn Promotion");
        dlg.setHeaderText("Choose a piece to promote to:");
        Optional<String> result = dlg.showAndWait();
        String choice = result.orElse("Queen");

        Piece newPiece = switch (choice) {
            case "Rook" -> new Rook(pawn.isWhite());
            case "Bishop" -> new Bishop(pawn.isWhite());
            case "Knight" -> new Knight(pawn.isWhite());
            default -> new Queen(pawn.isWhite());
        };

        // place the new piece
        newPiece.setPosition(m.toRow, m.toCol);
        b[m.toRow][m.toCol] = newPiece;
        newPiece.markMoved();
    }


    /**
     * Captures the pawn en passant
     */
    private void performEnPassant(Move m) {
        Piece[][] b = board.getBoard();
        Piece pawn = b[m.fromRow][m.fromCol];

        // move pawn
        b[m.toRow][m.toCol] = pawn;
        b[m.fromRow][m.fromCol] = null;

        // remove the passed pawn
        int capturedRow = m.fromRow;
        int capturedCol = m.toCol;
        b[capturedRow][capturedCol] = null;
    }


    private int[] findKing(boolean white, Piece[][] b) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = b[r][c];
                if (p instanceof King && p.isWhite() == white) {
                    return new int[]{r, c};
                }
            }
        }
        return null; // should never happen in a valid game
    }

    private boolean isSquareAttacked(int targetR, int targetC, boolean byWhite, Piece[][] b) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece attacker = b[r][c];
                if (attacker != null && attacker.isWhite() == byWhite) {
                    Move m = new Move(r, c, targetR, targetC);
                    // use basic validation (ignores exposing king to check)
                    if (attacker.isValidMove(m, b)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheck(boolean whiteToMove) {
        Piece[][] b = board.getBoard();
        int[] kingPos = findKing(whiteToMove, b);
        return isSquareAttacked(kingPos[0], kingPos[1], !whiteToMove, b);
    }

    public boolean hasLegalMoves(boolean whiteToMove) {
        Piece[][] b = board.getBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = b[r][c];
                if (p != null && p.isWhite() == whiteToMove) {
                    for (int tr = 0; tr < 8; tr++) {
                        for (int tc = 0; tc < 8; tc++) {
                            Move m = new Move(r, c, tr, tc);
                            if (!p.isValidMove(m, b)) continue;

                            // simulate
                            Piece captured = b[tr][tc];
                            b[tr][tc] = p;
                            b[r][c] = null;

                            boolean stillInCheck = isInCheck(whiteToMove);

                            // undo
                            b[r][c] = p;
                            b[tr][tc] = captured;

                            if (!stillInCheck) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    public boolean isCheckmate() {
        boolean inCheck = isInCheck(whiteTurn);
        boolean canMove = hasLegalMoves(whiteTurn);
        return inCheck && !canMove;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }
    /**
     * Generate all legal moves for the given side without mutating the real game.
     */
    public List<Move> generateLegalMoves(boolean forWhite) {
        List<Move> moves = new ArrayList<>();
        Piece[][] b = board.getBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = b[r][c];
                if (p != null && p.isWhite() == forWhite) {
                    // try every target square
                    for (int tr = 0; tr < 8; tr++) {
                        for (int tc = 0; tc < 8; tc++) {
                            Move m = new Move(r, c, tr, tc);
                            if (basicValidation(m)) {
                                moves.add(m);
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }

    /**
            * Create a deep‐copy of this GameManager: clones the board, turn, and lastMove.
            */
    @Override
    public GameManager clone() {
        // 1) Deep‐copy the board array via its constructor
        Board boardCopy = new Board(board.getBoard());
        // 2) Build a new manager
        GameManager copy = new GameManager(boardCopy);
        // 3) Copy turn and last‐move
        copy.whiteTurn = this.whiteTurn;
        copy.lastMove  = this.lastMove;
        return copy;
    }

}
