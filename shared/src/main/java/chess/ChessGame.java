package chess;

import java.util.ArrayList;
import java.util.Collection;

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
        //Check to see if any of the moves leave the king in danger
        //Remove those moves from the list
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //throw new RuntimeException("Not implemented");
        theBoard.addPiece(move.getEndPosition(), theBoard.getPiece(move.getStartPosition()));//Move/capture
        theBoard.addPiece(move.getStartPosition(), null);//Erase what was there before
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

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //If the king is in check AND the team has no valid moves
        //A helper function to check if the team has no valid moves would be good
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //If the king is NOT in check AND the team has no valid moves
        throw new RuntimeException("Not implemented");
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
