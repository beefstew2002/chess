package chess;

import java.util.ArrayList;

public class PawnMovesCalculator {
    private final ChessPosition startPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor color;

    public PawnMovesCalculator(ChessPosition startPosition, ChessBoard board, ChessGame.TeamColor color) {
        this.startPosition = startPosition;
        this.board = board;
        this.color=color;
    }



    public ArrayList<ChessMove> getMoves() {
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>(); //The array to be returned

        int up;
        if (this.color == ChessGame.TeamColor.BLACK) {
            up = -1;
        }else{
            up = 1;
        }
        ChessPosition pos;
        ChessPosition dir;

        //Advancing one space
        pos = startPosition.copy();
        dir = new ChessPosition(up,0);
        pos.add(dir);
        if (PieceMovesCalculator.isSquareEmpty(pos, board)) {
            moves.add(new ChessMove(startPosition, pos, ChessPiece.PieceType.PAWN));
        }

        //Initial big jump option
        /*
        if ((up == 1 && startPosition.getRow() == 2)||(up == -1 && startPosition.getRow() == 7)) {
            pos = startPosition.copy();
            dir = new ChessPosition(0,up);
            pos.add(dir);
            if (PieceMovesCalculator.isSquareEmpty(pos, board)) {
                pos.add(dir);
                if (PieceMovesCalculator.isSquareEmpty(pos, board)) {
                    moves.add(new ChessMove(startPosition, pos, ChessPiece.PieceType.PAWN));
                }
            }
        }*/

        //Diagonal capturing

        //No need to google En Passant
        //Wait, what about promotion? Do we assume pawns are always promoted to be queens?

        return moves;
    }
}
