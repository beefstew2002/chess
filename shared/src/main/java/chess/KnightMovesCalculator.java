package chess;

import java.util.ArrayList;

public class KnightMovesCalculator {
    private final ChessPosition startPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor color;

    public KnightMovesCalculator(ChessPosition startPosition, ChessBoard board, ChessGame.TeamColor color) {
        this.startPosition = startPosition;
        this.board = board;
        this.color=color;
    }

    public ArrayList<ChessMove> getMoves() {
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>(); //The array to be returned

        ChessPosition[] directions = new ChessPosition[8];
        directions[0] = new ChessPosition(-1,2);
        directions[1] = new ChessPosition(1,2);
        directions[2] = new ChessPosition(2,1);
        directions[3] = new ChessPosition(2,-1);
        directions[4] = new ChessPosition(1,-2);
        directions[5] = new ChessPosition(-1,-2);
        directions[6] = new ChessPosition(-2,1);
        directions[7] = new ChessPosition(-2,-1);

        ChessPosition pos;

        for (int i=0; i<8; i++) {
            pos = startPosition.copy();
            pos.add(directions[i]);
            if (PieceMovesCalculator.isSquareAvailable(pos, board, color)) {
                moves.add(new ChessMove(startPosition, pos, null));
            }
        }


        return moves;
    }
}
