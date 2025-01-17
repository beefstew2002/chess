package chess;

import java.util.ArrayList;

import static chess.ChessPiece.PieceType;

public class QueenMovesCalculator implements PieceMovesCalculator {

    //private final ChessPiece bishop;
    private final ChessPosition startPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor color;

    public QueenMovesCalculator(/*ChessPiece bishop, */ChessPosition startPosition, ChessBoard board, ChessGame.TeamColor color) {
        this.startPosition = startPosition;
        this.board = board;
        this.color=color;
    }

    public ArrayList<ChessMove> getMoves() {
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>(); //The array to be returned

        RookMovesCalculator rmc = new RookMovesCalculator(startPosition, board, color);
        BishopMovesCalculator bmc = new BishopMovesCalculator(startPosition, board, color);

        moves.addAll(rmc.getMoves());
        moves.addAll(bmc.getMoves());

        return moves;
    }
}
