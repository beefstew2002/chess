package chess;

import java.util.ArrayList;

public interface PieceMovesCalculator {

    public static boolean isSquareEmpty(ChessPosition position, ChessBoard board) {
        if (!position.inBounds()) {
            return false;
        }
        return board.getPiece(position) == null;
    }

    public static ArrayList<ChessMove> sendRay(ChessPosition startPosition, ChessBoard board, ChessGame.TeamColor color, ChessPosition direction) { //Check the squares stepping in a particular direction
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();

        var pos = startPosition.copy();
        boolean going = true;

        while (going) {
            pos.add(direction); //Advance to the next square
            if (isSquareEmpty(pos, board)) {
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

        return moves;
    }


    public default ArrayList<ChessMove> getMoves() {
        return new ArrayList<ChessMove>();
    }
}
