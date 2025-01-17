package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    int r;
    int c;

    public ChessPosition(int row, int col) {
        r = row;
        c = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        //throw new RuntimeException("Not implemented");
        return r;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        //throw new RuntimeException("Not implemented");
        return c;
    }

    public boolean equals(ChessPosition p) {
        return r==p.getRow() && c==p.getColumn();
    }

    public int hashCode() {
        return ((r*3) * (c+2));
    }
}
