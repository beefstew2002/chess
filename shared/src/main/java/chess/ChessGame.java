package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor whoseTurn;
    private ChessBoard theBoard;
    protected boolean experimental;

    public ChessGame() {
        whoseTurn = TeamColor.WHITE;
        theBoard = new ChessBoard();
        theBoard.resetBoard();
    }

    @Override
    public ChessGame clone() {
        ChessGame noob = new ChessGame();
        noob.setTeamTurn(whoseTurn);
        noob.setBoard(theBoard.clone());
        return noob;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return whoseTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        whoseTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //Get the set of possible moves for the piece
        ArrayList<ChessMove> moves = new ArrayList<>();
        ArrayList<ChessMove> allMoves = (ArrayList<ChessMove>) theBoard.getPiece(startPosition).pieceMoves(theBoard, startPosition);
        //Check to see if any of the moves leave the king in danger
        /*for (int i=0; i< allMoves.size(); i++) {
            if (isMoveValid(allMoves.get(i))) {
                moves.add(allMoves.get(i));
            }
        }*/
        for (ChessMove move : allMoves) {
            if (isMoveValid(move)) {
                moves.add(move);
            }
        }

        return moves;
    }

    //Method to check if a single move is valid without modifying the actual game
    private boolean isMoveValid(ChessMove move) {
        ChessGame testGame = this.clone();
        testGame.experimental = true; //this is to make it so it can claim a move is valid even if it's not that team's turn

        //This is going to have to create a copy of the entire game for each move that it tests
        //I honestly dk if there's a better way to do this, though
        //This seems like the most efficient, at least for me to write it
        try {
            testGame.makeMove(move);
        } catch (InvalidMoveException e) {
            return false;
        }

        return true;
    }

    //Helper function to check for check for castling moves
    private void avoidCheckInCastling(ChessPiece piece, ChessMove move) throws InvalidMoveException {
        //Checking for check with castle moves
        int row = switch(piece.getTeamColor()) {case TeamColor.WHITE -> 1; case TeamColor.BLACK -> 8;};
        if (move.getCastleMove() != ChessMove.CastleMoveType.none) {
            if (isInCheck(whoseTurn)) {
                throw new InvalidMoveException("You can't castle when you're in check!");
            }
            ChessGame testGame = this.clone();
            testGame.experimental = true;
            //Kingside
            if (move.getCastleMove() == ChessMove.CastleMoveType.kingside) {
                try {
                    testGame.makeMove(new ChessMove(move.getStartPosition(), new ChessPosition(row,6)));
                    testGame.makeMove(new ChessMove(new ChessPosition(row,6), new ChessPosition(row,7)));
                } catch (InvalidMoveException e) {
                    throw new InvalidMoveException("You can't castle through checked squares!");
                }
            }
            //Queenside
            else if (move.getCastleMove() == ChessMove.CastleMoveType.queenside) {
                try {
                    testGame.makeMove(new ChessMove(move.getStartPosition(), new ChessPosition(row, 4)));
                    testGame.makeMove(new ChessMove(new ChessPosition(row, 4), new ChessPosition(row, 3)));
                } catch (InvalidMoveException e) {
                    throw new InvalidMoveException("You can't castle through checked squares!");
                }
            }
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //Check if there's no piece at the start position
        if (theBoard.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("What? There's nothing there to move!");
        }
        ChessPiece piece = theBoard.getPiece(move.getStartPosition());
        //Identify castling moves and mark them for later
        if (piece.getPieceType() == ChessPiece.PieceType.KING && move.getStartPosition().getColumn() == 5) {
            if (move.getEndPosition().getColumn() == 7) {move.setCastleMove(ChessMove.CastleMoveType.kingside);}
            else if (move.getEndPosition().getColumn() == 3) {move.setCastleMove(ChessMove.CastleMoveType.queenside);}
        }
        //Identify big pawn jumps and mark them for later
        int pawnStartRow = switch(piece.getTeamColor()) { case WHITE -> 2; case BLACK -> 7;};
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && move.getStartPosition().getRow() == pawnStartRow) {
            int length = Math.abs(move.getStartPosition().getRow() - move.getEndPosition().getRow());
            if (length == 2) {
                move.setBigPawnJump(true);
            }
        }
        //Identify en passants and mark them for later
        if (move.getStartPosition().getColumn() != move.getEndPosition().getColumn() &&
                theBoard.getPiece(move.getEndPosition()) == null &&
                piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            //Sets a move as "en passant" if it ends in a different column than it starts,
            // the target square is empty, and the piece is a pawn
            move.setEnPassant(true);
        }
        //Shortcut: find out what piece is at that position, and make sure the move you're attempting is in the list
        ArrayList<ChessMove> possibleMoves = (ArrayList<ChessMove>) piece.pieceMoves(theBoard, move.getStartPosition());
        if (!possibleMoves.contains(move)) {
            throw new InvalidMoveException("Nice try, that piece can't do that!"); //This messes with en passant
        }
        //Check if the piece is on the team of whose turn it is
        if (piece.getTeamColor() != whoseTurn && !experimental) {
            throw new InvalidMoveException("That's not your piece!");
        }

        avoidCheckInCastling(piece, move);
        int row = switch(piece.getTeamColor()) {case TeamColor.WHITE -> 1; case TeamColor.BLACK -> 8;};

        //Try the move!
        if (move.getPromotionPiece() != null) {
            theBoard.addPiece(move.getEndPosition(), new ChessPiece(whoseTurn,move.getPromotionPiece()));//Move and promote
        } else {
            theBoard.addPiece(move.getEndPosition(), piece);//Move
        }
        theBoard.addPiece(move.getStartPosition(), null);//Erase what was there before
        theBoard.getPiece(move.getEndPosition()).itMoved();//Set that that piece moved, important for castling
        //Other castling details: moving the rooks
        if (move.getCastleMove() != ChessMove.CastleMoveType.none) {
            //Oh, maybe THIS is where it checks the validity of the move, yeah!
            if (move.getCastleMove() == ChessMove.CastleMoveType.kingside) {
                theBoard.addPiece(new ChessPosition(row, 6), theBoard.getPiece(new ChessPosition(row, 8))); //Add the rook to the side
                theBoard.addPiece(new ChessPosition(row, 8), null); //Erase it from where it was before
            }
            else if (move.getCastleMove() == ChessMove.CastleMoveType.queenside) {
                theBoard.addPiece(new ChessPosition(row, 4), theBoard.getPiece(new ChessPosition(row, 1))); //Add the rook to the side
                theBoard.addPiece(new ChessPosition(row, 1), null); //Erase it from where it was before
            }
        }
        //If the move was an en passant, capture that piece
        if (move.isEnPassant()) {
            int up = switch(piece.getTeamColor()) {case WHITE -> 1; case TeamColor.BLACK -> -1;};
            theBoard.addPiece(move.getEndPosition().getRow()-up,move.getEndPosition().getColumn(),null);
        }

        //If your king is now in check
        if (isInCheck(theBoard.getPiece(move.getEndPosition()).getTeamColor())) {
            //I need to replace whoseTurn here with the team of the original piece, not just whoever's turn it is
            //I really shouldn't have to do this, but here's some code to reset the board

            throw new InvalidMoveException("You left yourself in check!");
        }

        //After completing the move, set all pieces on the board to NOT be en passantable
        SquaresIterator sqs = theBoard.iterator();
        ChessPiece p;
        while (sqs.hasNext()) {
            p = theBoard.getPiece(sqs.next());
            if (p != null) {
                p.setEnPassantable(false);
            }
        }

        //If that was a big pawn jump, that pawn is now en passantable, otherwise it's not
        piece.setEnPassantable( move.getBigPawnJump() );

        //Update whose turn it is
        if (whoseTurn == TeamColor.WHITE) {whoseTurn = TeamColor.BLACK;}
        else {whoseTurn = TeamColor.WHITE;}
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //Create a helper function isInDanger to check if any given piece is in danger

        //Identify the king of the team
        //ChessPiece king;
        ChessPosition kingPosition = new ChessPosition(0,0);
        //Scan through every square of the board (feel like I'm doing that a bunch) to get a king of the color
        //Maybe I'll add an Iterator to ChessBoard
        //I did that, I should use it here now that I have. But eh, this is working fine
        ChessPosition targetPosition = new ChessPosition(0,0);
        ChessPiece targetPiece;
        for (int r=1; r<=8; r++) {
            for (int c=1; c<=8; c++) {
                targetPosition.setCol(c);
                targetPosition.setRow(r);
                targetPiece = theBoard.getPiece(targetPosition);
                if (targetPiece != null && targetPiece.getPieceType() == ChessPiece.PieceType.KING && targetPiece.getTeamColor() == teamColor) {
                    //king = targetPiece;
                    kingPosition = targetPosition.copy();
                }
            }
        }
        if (kingPosition.equals(new ChessPosition(0,0))) {return false;} //for a weird edge case where there is no king

        return isInDanger(kingPosition);
    }

    //Helper function to determine whether there are no valid moves
    public boolean noValidMoves(TeamColor teamColor) {
        Iterator<ChessPosition> it = theBoard.iterator();
        Collection<ChessMove> moves;
        ChessPosition cursor;
        while (it.hasNext()) {
            cursor = it.next();
            if (theBoard.getPiece(cursor) != null && theBoard.getPiece(cursor).getTeamColor() == teamColor) {
                moves = validMoves(cursor);
                if (!moves.isEmpty()) {

                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //If the king is in check AND the team has no valid moves
        //A helper function to check if the team has no valid moves would be good
        return isInCheck(teamColor) && noValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(TeamColor.WHITE) || isInCheck(TeamColor.BLACK)) {
            return false;
        }
        //If the king is NOT in check AND the team has no valid moves
        return !isInCheck(teamColor) && noValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        theBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return theBoard;
    }

    //Checks if the piece at a given position is in danger
    public boolean isInDanger(ChessPosition position) {
        //Identify the color of the piece at the position
        TeamColor myTeam = theBoard.getPiece(position).getTeamColor();
        TeamColor enemyColor;
        if (myTeam.equals(TeamColor.WHITE)) {enemyColor = TeamColor.BLACK;}
        else {enemyColor = TeamColor.WHITE;}

        //Loop through each of the pieces on the opposite team
        ChessPosition targetPosition = new ChessPosition(1,1);
        ChessPiece targetPiece;
        ArrayList<ChessMove> moves;

        SquaresIterator si = new SquaresIterator(theBoard);
        ChessPosition sq;
        while (si.hasNext()) {
            sq = si.next();
            //Look at the current square
            targetPosition.setRow(sq.getRow());
            targetPosition.setCol(sq.getColumn());
            targetPiece = theBoard.getPiece(targetPosition);
            if (targetPiece != null && (targetPiece.getTeamColor() == enemyColor)) {
                moves = (ArrayList<ChessMove>) targetPiece.pieceMoves(theBoard, targetPosition);
                /*for (int i = 0; i < moves.size(); i++) {
                    //If any pieces of the opposite team can move to this square
                    //Return true
                    if (moves.get(i).getEndPosition().equals(position)) {
                        return true;
                    }
                }*/
                for (ChessMove move : moves) {
                    if (move.getEndPosition().equals(position)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean equals(ChessGame otherGame) {
        return this.theBoard.equals(otherGame.getBoard());
    }

}
