package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Iterable<ChessPosition>{

    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //throw new RuntimeException("Not implemented");
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }
    //Overload
    public void addPiece(int r, int c, ChessPiece piece) {
        //throw new RuntimeException("Not implemented");
        addPiece(new ChessPosition(r,c), piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        //throw new RuntimeException("Not implemented");
        if (!position.inBounds()) {
            return null;
        }
        return squares[position.getRow()-1][position.getColumn()-1];
    }
    //Overload so I don't have to write new ChessPosition every time I have to call this
    public ChessPiece getPiece(int x, int y) {
        return getPiece(new ChessPosition(x,y));
    }

    private void addTeam(ChessGame.TeamColor color) {
        int left = 1;
        int right = 8;
        ChessPiece.PieceType type;

        int row = 0;
        int pawnRow = 0;

        if (color == ChessGame.TeamColor.WHITE) {
            row = 1;
            pawnRow = 2;
        }
        else if (color == ChessGame.TeamColor.BLACK) {
            row = 8;
            pawnRow = 7;
        }

        //Rooks
        type = ChessPiece.PieceType.ROOK;
        addPiece(new ChessPosition(row,left), new ChessPiece(color, type));
        addPiece(new ChessPosition(row,right), new ChessPiece(color, type));
        left++;right--;
        //Knights
        type = ChessPiece.PieceType.KNIGHT;
        addPiece(new ChessPosition(row,left), new ChessPiece(color, type));
        addPiece(new ChessPosition(row,right), new ChessPiece(color, type));
        left++;right--;
        //Bishops
        type = ChessPiece.PieceType.BISHOP;
        addPiece(new ChessPosition(row,left), new ChessPiece(color, type));
        addPiece(new ChessPosition(row,right), new ChessPiece(color, type));
        left++;right--;
        //Royalty
        addPiece(new ChessPosition(row,left), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row,right), new ChessPiece(color, ChessPiece.PieceType.KING));
        //Pawns
        for (int i=1; i<=8; i++) {
            addPiece(new ChessPosition(pawnRow, i), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //throw new RuntimeException("Not implemented");
        squares = new ChessPiece[8][8];
        ChessGame.TeamColor color;
        ChessPiece.PieceType type;
        int row = 1;
        int left = 1;
        int right = 8;
        //addPiece(new ChessPosition(0,0), new ChessPiece(color, type));

        addTeam(ChessGame.TeamColor.WHITE);
        addTeam(ChessGame.TeamColor.BLACK);

    }

    public ChessPiece[][] getSquares() {
        return squares;
    }

    public String toString() {
        String s = "";
        String row;
        for (int r=0; r<8; r++) {
            row = "|";
            for (int c=0; c<8; c++) {
                if (squares[r][c]!=null) {
                    row += squares[r][c].toString()+"|";
                }else{
                    row += " |";
                }
            }
            s = row + "\n" + s;
        }
        s = "\n" + s;
        return s;
    }

    public boolean equals(Object other) {
        ChessBoard otherBoard = (ChessBoard) other;
        return this.toString().equals(otherBoard.toString());
    }

    @Override
    public ChessBoard clone() {
        ChessBoard newb = new ChessBoard();

        Iterator<ChessPosition> it = new SquaresIterator(this);
        ChessPosition pos;

        while (it.hasNext()) {
            pos = it.next();
            if (getPiece(pos) != null) {newb.addPiece(pos, getPiece(pos).clone());}
        }

        return newb;
    }

    public SquaresIterator iterator() {
        return new SquaresIterator(this);
    }
}

class SquaresIterator implements Iterator<ChessPosition> {
    ChessPosition cursor;
    static ChessPosition right = new ChessPosition(0,1);
    static ChessPosition up = new ChessPosition(1,0);

    SquaresIterator(ChessBoard board) {
        cursor = new ChessPosition(1,1);
    }

    public boolean hasNext() {
        if (cursor.getRow() > 8) {
            return false;
        }
        return true;
    }

    public ChessPosition next() {
        ChessPosition tmp = cursor.copy();
        if (hasNext()) {
            if (cursor.getColumn()>=8) {
                cursor.setCol(1);
                cursor.add(up);
            }else{
                cursor.add(right);
            }
        }
        return tmp;
    }
}