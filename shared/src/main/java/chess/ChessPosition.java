package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int row;
    private int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        //throw new RuntimeException("Not implemented");
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        //throw new RuntimeException("Not implemented");
        return col;
    }

    //Adding setters for Row and Column to make the move calculators easier
    public void setRow(int r) {
        row = r;
    }
    public void setCol(int c) {
        col = c;
    }
    //In fact, to really make things easy, I'm gonna add a method to add an integer array to the position
    public void add(int[] vals) {
        row += vals[0];
        col += vals[1];
    }

    public boolean equals(ChessPosition p) {
        //return r==p.getRow() && c==p.getColumn();
        return true;
    }

    public int hashCode() {
        return (row*100) + (col);
    }

    public String toString() {
        return "["+row+", "+col+"]";
    }
}
