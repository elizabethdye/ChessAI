package chess.ai;

import java.util.EnumMap;

import chess.core.BitBoard;
import chess.core.BoardSquare;
import chess.core.ChessPiece;
import chess.core.Chessboard;
import chess.core.PieceColor;


public class BasicMaterial3 implements BoardEval {
	final static int MAX_VALUE = 20000;
	private EnumMap<ChessPiece,Integer> values = new EnumMap<ChessPiece,Integer>(ChessPiece.class);
	
	public BasicMaterial3() {
		values.put(ChessPiece.BISHOP, 325);
		values.put(ChessPiece.KNIGHT, 320);
		values.put(ChessPiece.PAWN, 100);
		values.put(ChessPiece.QUEEN, 975);
		values.put(ChessPiece.ROOK, 500);
		values.put(ChessPiece.KING, MAX_VALUE);
	}

	@Override
	public int eval(Chessboard board) {
		int total = 0;
		PieceColor mColor = board.getMoverColor();
		PieceColor oColor = board.getOpponentColor();
		BitBoard bishops = board.getAllOf(mColor, ChessPiece.BISHOP);
		if( bishops.numPieces() == 2){
			total += 15;
		}
		bishops = board.getAllOf(oColor, ChessPiece.BISHOP);
		if( bishops.numPieces() == 2){
			total -= 15;
		}
		int cTot = 0;
		cTot += colorHere(board, BoardSquare.D5, mColor);
		cTot += colorHere(board, BoardSquare.E5, mColor);
		cTot += colorHere(board, BoardSquare.D4, mColor);
		cTot += colorHere(board, BoardSquare.E4, mColor);
		total += (cTot * 10);
		
		for (BoardSquare s: board.allPieces()) {
			ChessPiece type = board.at(s);
			if (board.colorAt(s).equals(mColor)) {
				total += valueOfChessPiece(type);
			} else {
				total -= valueOfChessPiece(type);
			}
		}
		return total;
	}

	@Override
	public int maxValue() {
		return MAX_VALUE;
	}
	
	private int colorHere(Chessboard board, BoardSquare s, PieceColor mColor){
		try{
			PieceColor p = board.colorAt(s);
			if (p.equals(mColor)){
				return 1;
			}
			else{
				return -1;
			}
		}
		catch(IllegalStateException e){
			return 0;
		}
	}
	
	public int valueOfChessPiece(ChessPiece piece) {
		return values.containsKey(piece) ? values.get(piece) : 0;
	}
}
