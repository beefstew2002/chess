package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        //throw new RuntimeException("Not implemented");
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        //throw new RuntimeException("Not implemented");
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        //throw new RuntimeException("Not implemented");
        return promotionPiece;
    }

    public String toString() {
        //return Integer.toString(this.hashCode());

        String pp;
        if (promotionPiece == null) {
            pp = "";
        }else{
            pp = promotionPiece.toString();
        }
        return startPosition.toString() + " " + endPosition.toString() + pp;

    }

    public int hashCode() {
        int pp;
        if (promotionPiece != null) {
            pp = promotionPiece.ordinal() + 1;
        }else{
            pp = 0;
        }
        return (pp*10000)+startPosition.hashCode()*100+endPosition.hashCode();
    }

    public boolean equals(Object otherMove) {
        return this.hashCode() == otherMove.hashCode();
    }

}
