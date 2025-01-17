package chess;

import java.util.ArrayList;

import static chess.ChessPiece.PieceType;

public class RookMovesCalculator implements PieceMovesCalculator {

    //private final ChessPiece bishop;
    private final ChessPosition startPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor color;

    public RookMovesCalculator(/*ChessPiece bishop, */ChessPosition startPosition, ChessBoard board, ChessGame.TeamColor color) {
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
                case 0 -> direction.set(1,0);
                case 1 -> direction.set(0,-1);
                case 2 -> direction.set(-1,0);
                case 3 -> direction.set(0,1);
            }
            /*
            pos = startPosition.copy();
            going = true;

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
            }*/

            moves.addAll(PieceMovesCalculator.sendRay(startPosition, board, color, direction));

        }

        return moves;
    }
}
