package chess.model;

public class Move {
    public final int fromRow;
    public final int fromCol;
    public final int toRow;
    public final int toCol;

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    public static String notation(Move m) {
        char fromFile = (char) ('a' + m.fromCol);
        char toFile = (char) ('a' + m.toCol);
        int fromRank = 8 - m.fromRow;
        int toRank = 8 - m.toRow;
        return "" + fromFile + fromRank + "→" + toFile + toRank;
    }


}

