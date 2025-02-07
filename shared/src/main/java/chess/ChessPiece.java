package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private boolean hasMoved;
    private boolean enPassantable = false;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.hasMoved = false;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    //Returns the hasMoved boolean, although the code to manage that is actually in ChessGame
    public boolean hasPieceMoved() {
        return hasMoved;
    }
    public void itMoved() {
        hasMoved = true;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //throw new RuntimeException("Not implemented");
        switch (type) {
            case BISHOP -> {
                BishopMovesCalculator bmc = new BishopMovesCalculator(myPosition, board, pieceColor);
                return bmc.getMoves();
            }
            case KING -> {
                KingMovesCalculator kmc = new KingMovesCalculator(myPosition, board, pieceColor);
                kmc.setHasMoved(hasMoved);

                return kmc.getMoves();
            }
            case KNIGHT -> {
                KnightMovesCalculator kmc = new KnightMovesCalculator(myPosition, board, pieceColor);
                return kmc.getMoves();
            }
            case PAWN -> {
                PawnMovesCalculator pmc = new PawnMovesCalculator(myPosition, board, pieceColor);
                return pmc.getMoves();
            }
            case ROOK -> {
                RookMovesCalculator rmc = new RookMovesCalculator(myPosition, board, pieceColor);
                return rmc.getMoves();
            }
            case QUEEN -> {
                QueenMovesCalculator qmc = new QueenMovesCalculator(myPosition, board, pieceColor);
                return qmc.getMoves();
            }
        }
        return new ArrayList<>();
    }

    //En passantable
    public boolean isEnPassantable() {
        return this.enPassantable;
    }
    public void setEnPassantable(boolean b) {
        this.enPassantable = b;
    }

    public String toString() {
        String c = switch(pieceColor) {
            case WHITE -> "W";
            case BLACK -> "B";
        };
        String t = switch(type) {
            case PAWN -> "P";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case QUEEN -> "Q";
            case KING -> "K";
        };
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            t = t.toLowerCase(Locale.ROOT);
        }

        return /*c +*/ t;
    }

    public boolean equals(Object piece) {
        return this.toString().equals(piece.toString());
    }

    @Override
    public ChessPiece clone() {
        ChessPiece cp = new ChessPiece(pieceColor, type);
        cp.setEnPassantable(this.enPassantable);
        return cp;

    }
}
