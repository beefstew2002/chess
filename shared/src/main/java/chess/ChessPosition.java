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
    public void set(int r, int c) {
        this.row = r;
        this.col = c;
    }
    //In fact, to really make things easy, I'm gonna add a method to add another position to the position
    public void add(ChessPosition dir) {
        this.row += dir.getRow();
        this.col += dir.getColumn();

    }
    //...adding a copy method...
    public ChessPosition copy() {
        return new ChessPosition(row, col);
    }
    //Also adding an inBounds function to check whether the position exists on the 8x8 grid
    public boolean inBounds() {
        return row > 0 && row <= 8 && col > 0 && col <= 8;
    }

    public boolean equals(ChessPosition p) {
        return row==p.getRow() && col==p.getColumn();
        //return true;
    }

    public int hashCode() {
        return (row*100) + (col);
    }

    public String toString() {
        return "["+row+", "+col+"]";
    }
}
