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

    private ArrayList<ChessMove> jumpsAndPromotions(ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        boolean promote = false;
        ChessPiece.PieceType[] promotables = {
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.ROOK
        };
        if ((color == ChessGame.TeamColor.BLACK && position.getRow()==1)||(color == ChessGame.TeamColor.WHITE && position.getRow()==8)) {
            promote = true;
        }

        if (promote) {
            for (int i=0; i< promotables.length; i++) {
                moves.add(new ChessMove(startPosition, position, promotables[i]));
            }
        }else{
            moves.add(new ChessMove(startPosition, position, null));
        }
        return moves;
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
            //moves.add(new ChessMove(startPosition, pos, ChessPiece.PieceType.PAWN));
            moves.addAll(jumpsAndPromotions(pos));
        }

        //Initial big jump option
        //(I could optimize by combining this more elegantly with the default advance-by-one)
        if ((up == 1 && startPosition.getRow() == 2)||(up == -1 && startPosition.getRow() == 7)) {
            pos = startPosition.copy();
            dir = new ChessPosition(up,0);
            pos.add(dir);
            if (PieceMovesCalculator.isSquareEmpty(pos, board)) {
                pos.add(dir);
                if (PieceMovesCalculator.isSquareEmpty(pos, board)) {
                    ChessMove bigJump = new ChessMove(startPosition, pos);
                    bigJump.setBigPawnJump(true);
                    moves.add(bigJump);
                    //moves.addAll(jumpsAndPromotions(pos)); //This doesn't work for en passant and is unnecessary
                }
            }
        }/**/

        //Diagonal capturing
        pos = startPosition.copy();
        dir = new ChessPosition(up,-1);
        pos.add(dir);
        if (!PieceMovesCalculator.isSquareEmpty(pos, board)&&PieceMovesCalculator.isSquareAvailable(pos,board,color)) {
            //moves.add(new ChessMove(startPosition, pos, ChessPiece.PieceType.PAWN));
            moves.addAll(jumpsAndPromotions(pos));
        }
        //En passant
        ChessPiece enPassantTarget = board.getPiece(startPosition.getRow(),startPosition.getColumn()-1);
        if (board.getPiece(pos) == null && enPassantTarget != null && enPassantTarget.isEnPassantable()) {
            ChessMove enp = new ChessMove(startPosition,pos);
            enp.setEnPassant(true);
            moves.add(enp);
        }
        pos = startPosition.copy();
        dir = new ChessPosition(up,1);
        pos.add(dir);
        if (!PieceMovesCalculator.isSquareEmpty(pos, board)&&PieceMovesCalculator.isSquareAvailable(pos,board,color)) {
            //moves.add(new ChessMove(startPosition, pos, ChessPiece.PieceType.PAWN));
            moves.addAll(jumpsAndPromotions(pos));
        }
        //En passant
        enPassantTarget = board.getPiece(startPosition.getRow(),startPosition.getColumn()+1);
        if (board.getPiece(pos) == null && enPassantTarget != null && enPassantTarget.isEnPassantable()) {
            ChessMove enp = new ChessMove(startPosition,pos);
            enp.setEnPassant(true);
            moves.add(enp);
        }

        return moves;
    }
}
