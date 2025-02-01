package chess;

import java.lang.reflect.Array;
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

    public ChessGame() {
        whoseTurn = TeamColor.WHITE;
        theBoard = new ChessBoard();
        theBoard.resetBoard();
    }

    @Override
    public ChessGame clone() {
        ChessGame noob = new ChessGame();
        noob.setTeamTurn(whoseTurn);
        noob.setBoard(theBoard);
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
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        ArrayList<ChessMove> all_moves = (ArrayList<ChessMove>) theBoard.getPiece(startPosition).pieceMoves(theBoard, startPosition);
        //Check to see if any of the moves leave the king in danger
        for (int i=0; i< all_moves.size(); i++) {
            if (isMoveValid(all_moves.get(i))) {
                moves.add(all_moves.get(i));
            }
        }

        return moves;
    }

    //Method to check if a single move is valid without modifying the actual game
    private boolean isMoveValid(ChessMove move) {
        ChessGame testGame = this.clone();
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
        //Shortcut: find out what piece is at that position, and make sure the move you're attempting is in the list
        ChessPiece piece = theBoard.getPiece(move.getStartPosition());
        ArrayList<ChessMove> possible_moves = (ArrayList<ChessMove>) piece.pieceMoves(theBoard, move.getStartPosition());
        if (!possible_moves.contains(move)) {
            throw new InvalidMoveException("Nice try, that piece can't do that!");
        }
        //Check if the piece is on the team of whose turn it is
        if (piece.getTeamColor() != whoseTurn) {
            //throw new InvalidMoveException("That's not your piece!");
        }

        //Try the move!
        if (move.getPromotionPiece() != null) {
            theBoard.addPiece(move.getEndPosition(), new ChessPiece(whoseTurn,move.getPromotionPiece()));//Move and promote
        } else {
            theBoard.addPiece(move.getEndPosition(), piece);//Move
        }
        theBoard.addPiece(move.getStartPosition(), null);//Erase what was there before

        //If your king is now in check
        if (isInCheck(whoseTurn)) {
            throw new InvalidMoveException("You left yourself in check!");
        }

        //Update whose turn it is
        if (whoseTurn == TeamColor.WHITE) whoseTurn = TeamColor.BLACK;
        else whoseTurn = TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //Create a helper function isInDanger to check if any given piece is in danger
        //throw new RuntimeException("Not implemented");

        //Identify the king of the team
        //ChessPiece king;
        ChessPosition kingPosition = new ChessPosition(0,0);
        //Scan through every square of the board (feel like I'm doing that a bunch) to get a king of the color
        //Maybe I'll add an Iterator to ChessBoard
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
        if (myTeam.equals(TeamColor.WHITE)) enemyColor = TeamColor.BLACK;
        else enemyColor = TeamColor.WHITE;

        //Loop through each of the pieces on the opposite team
        ChessPosition targetPosition = new ChessPosition(1,1);
        ChessPiece targetPiece;
        ArrayList<ChessMove> moves;
        for (int x=1; x<=8; x++) { for (int y=1; y<=8; y++) {
            //Look at the current square
            targetPosition.setRow(y);
            targetPosition.setCol(x);
            targetPiece = theBoard.getPiece(targetPosition);
            if (targetPiece != null && targetPiece.getTeamColor() == enemyColor) {
                moves = (ArrayList<ChessMove>) targetPiece.pieceMoves(theBoard, targetPosition);
                for (int i=0; i<moves.size(); i++) {
                    //If any pieces of the opposite team can move to this square
                    //Return true
                    if (moves.get(i).getEndPosition().equals(position)) {
                        return true;
                    }
                }
            }

        }}
        return false;
    }
}
