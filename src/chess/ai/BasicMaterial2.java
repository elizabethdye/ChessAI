package chess.ai;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

import chess.core.BoardSquare;
import chess.core.ChessPiece;
import chess.core.Chessboard;
import chess.core.PieceColor;


public class BasicMaterial2 implements BoardEval {
	final static int MAX_VALUE = 20000;
	
	private HashMap<String, Integer> letters = new HashMap<>();
	private EnumMap<ChessPiece,Integer> values = new EnumMap<ChessPiece,Integer>(ChessPiece.class);
	private ChessPiece[] dTypes = {ChessPiece.QUEEN, ChessPiece.ROOK, ChessPiece.BISHOP};
	
	public BasicMaterial2() {
		values.put(ChessPiece.BISHOP, 325);
		values.put(ChessPiece.KNIGHT, 320);
		values.put(ChessPiece.PAWN, 100);
		values.put(ChessPiece.QUEEN, 975);
		values.put(ChessPiece.ROOK, 500);
		values.put(ChessPiece.KING, MAX_VALUE);
		
		letters.put("A", 8);
		letters.put("B", 7);
		letters.put("C", 6);
		letters.put("D", 5);
		letters.put("E", 4);
		letters.put("F", 3);
		letters.put("G", 2);
		letters.put("H", 1);
	}

	@Override
	public int eval(Chessboard board) {
		int total = 0;
		BoardSquare kingBLoc = board.kingAt(PieceColor.BLACK);
		BoardSquare kingWLoc = board.kingAt(PieceColor.WHITE);
		for (BoardSquare s: board.allPieces()) {
			ChessPiece type = board.at(s);
			if (values.containsKey(type)) {
				PieceColor mColor = board.getMoverColor();
				if (board.colorAt(s).equals(mColor)) {
					float d;
					if (mColor.equals(PieceColor.BLACK)){
						d = distanceBetween(s, kingWLoc);
					}
					else{
						d = distanceBetween(s, kingBLoc);
					}
					if(d < 5.5){
						total += 1.5*values.get(type);
					}
					else{
						total += values.get(type);
					}
				} else {
					float d;
					if (mColor.equals(PieceColor.BLACK)){
						d = distanceBetween(s, kingWLoc);
					}
					else{
						d = distanceBetween(s, kingBLoc);
					}
					if(d < 5.5){
						total -= 1.5*values.get(type);
					}
					else{
						total -= values.get(type);
					}
				}
			}
		}
		return total;
	}

	@Override
	public int maxValue() {
		return MAX_VALUE;
	}
	
	public boolean hasValue(ChessPiece piece) {
		return values.containsKey(piece);
	}
	
	public int valueOf(ChessPiece piece) {
		return values.get(piece);
	}
	
	public float distanceBetween(BoardSquare s, BoardSquare king){
		int[] coords = getCoords(s);
		int[] coordk = getCoords(king);
		float distance = (float) Math.sqrt(Math.pow((coords[0] - coordk[1]),2) + Math.pow(coords[1]-coordk[1],2));
		return distance;
	}
	
	private int[] getCoords(BoardSquare square){
		String s = square.toString().toUpperCase();
//		System.out.println(s + "*******"+s.valueOf(s.charAt(0))+ "*******"+letters.get());
		int i = letters.get(s.valueOf(s.charAt(0)));
		int[] r = {i, Integer.valueOf(s.charAt(1))};
		return r;
	}
}
