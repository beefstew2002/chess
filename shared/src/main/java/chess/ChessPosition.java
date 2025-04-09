package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int row;
    private int column;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.column = col;
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
        return column;
    }

    //Adding setters for Row and Column to make the move calculators easier
    public void setRow(int r) {
        row = r;
    }
    public void setCol(int c) {
        column = c;
    }
    public void set(int r, int c) {
        this.row = r;
        this.column = c;
    }
    //In fact, to really make things easy, I'm gonna add a method to add another position to the position
    public void add(ChessPosition dir) {
        this.row += dir.getRow();
        this.column += dir.getColumn();

    }
    //...adding a copy method...
    public ChessPosition copy() {
        return new ChessPosition(row, column);
    }
    //Also adding an inBounds function to check whether the position exists on the 8x8 grid
    public boolean inBounds() {
        return row > 0 && row <= 8 && column > 0 && column <= 8;
    }

    public boolean equals(Object p) {
        return this.hashCode()==p.hashCode();
        //return true;
    }

    public int hashCode() {
        return (row*10) + (column);
    }

    public String toString() {
        return "["+row+", "+ column +"]";
    }
}
