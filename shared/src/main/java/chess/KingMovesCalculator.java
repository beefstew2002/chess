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
        /*
        //Check if it's in the right spot
        boolean rightSpot = false;
        int row = switch (color) {
            case WHITE -> 1;
            case BLACK -> 8;
        };
        rightSpot = startPosition.equals(new ChessPosition(row,5));

        //Check if the king is in check
        ChessGame testGame = new ChessGame();
        testGame.setBoard(board);
        testGame.experimental = true;
        if (!hasMoved && rightSpot && !checkingForCheck) {
            boolean isRookThere = false;
            boolean hasRookMoved = true;
            boolean areSpacesEmpty = false;
            boolean areSpacesSafe = true;
            //Kingside
             //Check if the rook is there and hasn't moved
            ChessPiece pieceThere = board.getPiece(new ChessPosition(row,8));
            if (pieceThere != null && pieceThere.getPieceType().equals(ChessPiece.PieceType.ROOK) && pieceThere.getTeamColor() == color) {
                isRookThere = true;
            }
            if (!pieceThere.hasPieceMoved()) {
                hasRookMoved = false;
            }
             //Check if the spaces are empty
            if (board.getPiece(new ChessPosition(row, 6)) == null && board.getPiece(new ChessPosition(row, 7)) == null) {
                areSpacesEmpty = true;
            }
             //Check if the spaces are safe
            testGame = new ChessGame();
            testGame.setBoard(board);
            testGame.experimental=true;
            try {
                testGame.makeMove(new ChessMove(startPosition, new ChessPosition(row, 6)));
                testGame.setBoard(board);
                testGame.makeMove(new ChessMove(startPosition, new ChessPosition(row, 6)));
            } catch (InvalidMoveException e) {
                areSpacesSafe = false;
            }
             //Add move to the list of possible moves
            if (isRookThere && !hasRookMoved && areSpacesEmpty && areSpacesSafe) {
                moves.add(new ChessMove(startPosition, new ChessPosition(row,7)));
            }

            //Queenside
             //Check if the rook is there and hasn't moved
             //Check if the spaces are empty
             //Check if the spaces are safe
             //Add move to the list of possible moves
        }
        */ //First attempt at making castling work

        //Second attempt
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
                ck.setCastleMove(ChessMove.castleMoveType.kingside);
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
                cq.setCastleMove(ChessMove.castleMoveType.queenside);
                moves.add(cq); // Queenside
            }
        }

        return moves;
    }
}
