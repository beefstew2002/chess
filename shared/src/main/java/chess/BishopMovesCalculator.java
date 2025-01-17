package chess;

import java.util.ArrayList;

import static chess.ChessPiece.PieceType;

public class BishopMovesCalculator {

    //private final ChessPiece bishop;
    private final ChessPosition startPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor color;

    public BishopMovesCalculator(/*ChessPiece bishop, */ChessPosition startPosition, ChessBoard board, ChessGame.TeamColor color) {
        //this.bishop = bishop;
        this.startPosition = startPosition;
        this.board = board;
        this.color=color;
    }

    public ArrayList<ChessMove> getMoves() {
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>(); //The array to be returned
        ChessPosition direction = new ChessPosition(1,1);
        ChessPosition pos;
        boolean going; //this is used to control when to stop tracing the path

        for (int i=0; i<4; i++) { //Loop through the same instruction (diagonal raytrace) 4 times
            switch (i) { //Set the direction it checks depending on the iteration of the loop
                case 0 -> direction.set(1,1);
                case 1 -> direction.set(1,-1);
                case 2 -> direction.set(-1,-1);
                case 3 -> direction.set(-1,1);
            }
            pos = startPosition.copy();
            going = true;


            /*do {
                moves.add(new ChessMove(startPosition, pos.copy(), PieceType.BISHOP));//If it's empty, add it to the array of possible squares
                pos.add(direction); //Advance to the next square
            } while ((board.getPiece(pos)==null) && pos.inBounds()); //As long as it hits squares that are empty and in bounds
            */
            while (going) {
                pos.add(direction); //Advance to the next square
                if ((board.getPiece(pos)==null) && pos.inBounds()) {
                    moves.add(new ChessMove(startPosition, pos.copy(), null));//If it's empty, add it to the array of possible squares
                }else{
                    if (board.getPiece(pos)!=null) {
                        if (board.getPiece(pos).getTeamColor()!=color) {
                            moves.add(new ChessMove(startPosition, pos.copy(), null));
                        }
                    }
                    going=false;
                }
            }
        }

        return moves;
    }
}
