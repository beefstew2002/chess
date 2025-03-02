package chess;

import java.util.ArrayList;

public class KingMovesCalculator {
    private final ChessPosition startPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor color;
    private boolean hasMoved = false;
    public boolean checkingForCheck = false;

    public KingMovesCalculator(ChessPosition startPosition, ChessBoard board, ChessGame.TeamColor color) {
        this.startPosition = startPosition;
        this.board = board;
        this.color=color;
    }

    public void setHasMoved(boolean h) {hasMoved = h;}

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
                    moves.add(new ChessMove(startPosition, pos, null));
                }
            }
        }

        //Castling options
        //Start with always making going to castle an option
        //Then narrow down when it's possible to do that
        if (!hasMoved) {
            int row = switch (color) {
                case WHITE -> 1;
                case BLACK -> 8;
            };
            ChessPiece maybeRook;
            boolean noPiecesInTheWay = true;
            //Kingside
            //Check if the spaces between it are empty
            if (board.getPiece(row,6) != null || board.getPiece(row,7) != null) {
                noPiecesInTheWay = false;
            }
            //Check if the kingside rook is there or has moved
            maybeRook = board.getPiece(row, 8);
            if (maybeRook != null && maybeRook.getPieceType() == ChessPiece.PieceType.ROOK && !maybeRook.hasPieceMoved() && noPiecesInTheWay) {
                ChessMove ck = new ChessMove(startPosition, new ChessPosition(row, 7));
                ck.setCastleMove(ChessMove.CastleMoveType.kingside);
                moves.add(ck); // Kingside
            }
            //Queenside
            //Check if the spaces between them are empty
            noPiecesInTheWay = true;
            if (board.getPiece(row,2) != null || board.getPiece(row,3) != null || board.getPiece(row,4) != null) {
                noPiecesInTheWay = false;
            }
            //Check if the queenside rook is there or has moved
            maybeRook = board.getPiece(row, 1);
            if (maybeRook != null && maybeRook.getPieceType() == ChessPiece.PieceType.ROOK && !maybeRook.hasPieceMoved() && noPiecesInTheWay) {
                ChessMove cq = new ChessMove(startPosition, new ChessPosition(row, 3));
                cq.setCastleMove(ChessMove.CastleMoveType.queenside);
                moves.add(cq); // Queenside
            }
        }

        return moves;
    }
}
