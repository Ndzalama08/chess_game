package chess.logic;

import chess.model.Board;
import chess.model.Move;
import chess.model.Piece;
import chess.model.pieces.*;
import chess.util.MoveValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GameManager implements Cloneable {
    private final Board board;
    private boolean whiteTurn = true;
    private Move lastMove = null;   // track for en passant

    // Half-moves since the last pawn move or capture (package-private for tests).
    int halfmoveClock = 0;
    // Position keys after every move, for threefold repetition.
    private final List<String> positions = new ArrayList<>();

    // isWhite -> chosen piece name ("Queen"/"Rook"/"Bishop"/"Knight"). Defaults
    // to auto-Queen so headless callers (AI search, tests) don't need a UI.
    private Function<Boolean, String> promotionChooser = isWhite -> "Queen";

    public GameManager(Board board) {
        this.board = board;
    }

    public void setPromotionChooser(Function<Boolean, String> promotionChooser) {
        this.promotionChooser = promotionChooser;
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
    private boolean isValid(Move m) {
        Piece[][] b = board.getBoard();
        Piece p = b[m.fromRow][m.fromCol];

        // 1) Must move your own piece
        if (p == null || p.isWhite() != whiteTurn) return false;

        boolean castling = MoveValidator.isCastling(m, b);
        boolean enPassant = MoveValidator.isEnPassant(m, b);

        // 2) Shape/path valid per piece logic — special moves (castling, en
        //    passant) don't match a piece's normal move shape, so they're
        //    validated separately below instead of falling into this check.
        if (!castling && !enPassant && !p.isValidMove(m, b)) return false;

        // 3) Cannot capture own piece
        Piece dest = b[m.toRow][m.toCol];
        if (dest != null && dest.isWhite() == whiteTurn) return false;

        // 4a) Castling
        if (castling && !MoveValidator.canCastle(m, b, whiteTurn)) return false;

        // 4b) En passant
        if (enPassant && !MoveValidator.canEnPassant(m, b, lastMove)) return false;

        // 5) Must not leave own king in check
        //    simulate the move; en passant also lifts the passed pawn off the
        //    board, which can open a rank onto the king
        Piece passed = enPassant ? b[m.fromRow][m.toCol] : null;
        b[m.toRow][m.toCol] = p;
        b[m.fromRow][m.fromCol] = null;
        if (enPassant) b[m.fromRow][m.toCol] = null;
        boolean leavesKingInCheck = isInCheck(whiteTurn);
        // undo
        b[m.fromRow][m.fromCol] = p;
        b[m.toRow][m.toCol] = dest;
        if (enPassant) b[m.fromRow][m.toCol] = passed;
        if (leavesKingInCheck) return false;

        return true;
    }

    /**
     * Attempt to move; returns true if valid and performed.
     * Also updates lastMove and flips turn.
     */
    public boolean attemptMove(Move m) {
        if (!isValid(m)) {
            return false;
        }

        Piece[][] b = board.getBoard();
        Piece p = b[m.fromRow][m.fromCol];
        Piece dest = b[m.toRow][m.toCol];
        boolean resetsClock = p instanceof Pawn || dest != null;

        // first move: remember the starting position (the board may have been
        // set up after construction, so it can't be captured earlier)
        if (positions.isEmpty()) positions.add(positionKey());

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
        halfmoveClock = resetsClock ? 0 : halfmoveClock + 1;
        positions.add(positionKey());
        return true;
    }

    /**
     * Piece placement, side to move and which kings/rooks can still castle.
     * ponytail: ignores en passant availability, which can only matter for
     * repetition in the rare case an en passant capture was possible once.
     */
    private String positionKey() {
        StringBuilder sb = new StringBuilder(80);
        for (Piece[] row : board.getBoard()) {
            for (Piece p : row) {
                if (p == null) { sb.append('.'); continue; }
                String n = p.getClass().getSimpleName().substring(0, 2);
                sb.append(p.isWhite() ? n.toUpperCase() : n);
                if ((p instanceof King || p instanceof Rook) && p.hasMoved()) sb.append('*');
            }
        }
        return sb.append(whiteTurn).toString();
    }

    public boolean isDrawByRepetition() {
        if (positions.isEmpty()) return false;
        String current = positions.get(positions.size() - 1);
        return positions.stream().filter(current::equals).count() >= 3;
    }

    /** Fifty full moves (100 half-moves) without a pawn move or capture. */
    public boolean isDrawByFiftyMoves() {
        return halfmoveClock >= 100;
    }

    /** K v K, K+minor v K, or only bishops that all stand on one square colour. */
    public boolean isDrawByInsufficientMaterial() {
        int minors = 0;
        int lightBishops = 0, darkBishops = 0, others = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board.getBoard()[r][c];
                if (p == null || p instanceof King) continue;
                if (p instanceof Bishop) {
                    if ((r + c) % 2 == 0) lightBishops++; else darkBishops++;
                } else if (p instanceof Knight) {
                    minors++;
                } else {
                    others++;   // pawn, rook or queen can always mate
                }
            }
        }
        if (others > 0) return false;
        int bishops = lightBishops + darkBishops;
        if (bishops == 0) return minors <= 1;
        return minors == 0 && (lightBishops == 0 || darkBishops == 0);
    }

    /**
     * Why the game is drawn right now, or null if it isn't. Single place the
     * UI asks, so a new draw rule needs no controller change.
     */
    public String drawReason() {
        if (isStalemate()) return "stalemate";
        if (isDrawByInsufficientMaterial()) return "insufficient material";
        if (isDrawByFiftyMoves()) return "the fifty-move rule";
        if (isDrawByRepetition()) return "threefold repetition";
        return null;
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

        String choice = promotionChooser.apply(pawn.isWhite());

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
        // Test positions and partially-set-up boards can lack a king; a side
        // with no king is not in check rather than a crash.
        if (kingPos == null) return false;
        return isSquareAttacked(kingPos[0], kingPos[1], !whiteToMove, b);
    }

    /**
     * Delegates to generateLegalMoves so castling and en passant count as
     * escapes: the hand-rolled scan this replaced only tried piece move
     * shapes, so a position whose sole legal reply was a special move was
     * wrongly reported as mate or stalemate.
     */
    public boolean hasLegalMoves(boolean whiteToMove) {
        return !generateLegalMoves(whiteToMove).isEmpty();
    }


    public boolean isCheckmate() {
        boolean inCheck = isInCheck(whiteTurn);
        boolean canMove = hasLegalMoves(whiteTurn);
        return inCheck && !canMove;
    }

    /** Draw: the side to move is not in check but has no legal move. */
    public boolean isStalemate() {
        return !isInCheck(whiteTurn) && !hasLegalMoves(whiteTurn);
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
        // isValid() judges moves for whoever is to move, so ask it about the
        // requested side and put the real turn back afterwards.
        boolean savedTurn = whiteTurn;
        whiteTurn = forWhite;
        try {
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Piece p = b[r][c];
                    if (p != null && p.isWhite() == forWhite) {
                        // try every target square
                        for (int tr = 0; tr < 8; tr++) {
                            for (int tc = 0; tc < 8; tc++) {
                                Move m = new Move(r, c, tr, tc);
                                if (isValid(m)) {
                                    moves.add(m);
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            whiteTurn = savedTurn;
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
        copy.halfmoveClock = this.halfmoveClock;
        copy.positions.addAll(this.positions);
        return copy;
    }

}
