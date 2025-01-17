package chess;

import java.util.ArrayList;

public class KingMovesCalculator {
    private final ChessPosition startPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor color;

    public KingMovesCalculator(ChessPosition startPosition, ChessBoard board, ChessGame.TeamColor color) {
        this.startPosition = startPosition;
        this.board = board;
        this.color=color;
    }

    public ArrayList<ChessMove> getMoves() {
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>(); //The array to be returned

        //Code to calculate the king's moves
        //Start checking -1 -1 of the king's position
        ChessPosition pos;
        //Loop through the 3x3 grid around the king
        for (int x=-1; x<=1; x++) {
            for (int y=-1; y<=1; y++) {
                pos = startPosition.copy();
                pos.add(new ChessPosition(x,y));
                if (PieceMovesCalculator.isSquareAvailable(pos, board, color)) {
                    moves.add(new ChessMove(startPosition, pos, ChessPiece.PieceType.KING));
                }
            }
        }

        return moves;
    }
}
