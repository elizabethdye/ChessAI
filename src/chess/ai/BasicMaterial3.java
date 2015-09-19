package chess.ai;

import java.util.EnumMap;

import chess.core.BoardSquare;
import chess.core.ChessPiece;
import chess.core.Chessboard;


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
		//values.put(ChessPiece.BISHOP, 3);
		//values.put(ChessPiece.KNIGHT, 3);
		//values.put(ChessPiece.PAWN, 1);
		//values.put(ChessPiece.QUEEN, 9);
		//values.put(ChessPiece.ROOK, 5);
		//values.put(ChessPiece.KING, MAX_VALUE);
	}

	@Override
	public int eval(Chessboard board) {
		int total = 0;
		for (BoardSquare s: board.allPieces()) {
			ChessPiece type = board.at(s);
			if (board.colorAt(s).equals(board.getMoverColor())) {
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
	
	public int valueOfChessPiece(ChessPiece piece) {
		return values.containsKey(piece) ? values.get(piece) : 0;
	}
}
