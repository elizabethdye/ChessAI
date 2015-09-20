package chess.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import chess.core.Chessboard;
import chess.core.Move;

public class Improved extends Searcher {
	private final boolean DEBUG = false;
	
	@Override
	public MoveScore findBestMove(Chessboard board, BoardEval eval, int depth) {
		setup(board, eval, depth);
		MoveScore result = evalMoves(board, eval, depth, -1, 1);
		tearDown();
		return result;   
	}
	
	MoveScore evalMoves(Chessboard board, BoardEval eval, int depth, int alpha, int beta) {
		if(depth <= 1) {return singularExtension(board, eval);}
		
		MoveScore best = null;
		for (Move m: reorder(board.getLegalMoves())) {
			Chessboard next = generate(board, m);
			MoveScore result = new MoveScore(-evalBoard(next, eval, depth - 1, -beta, -alpha), m);
			if (best == null || result.getScore() > best.getScore()) {
				best = result;
			}
		}
		return best;
	}
	
	int evalBoard(Chessboard board, BoardEval eval, int depth, int alpha, int beta) {
		if (!board.hasKing(board.getMoverColor()) || board.isCheckmate()) {
			return -eval.maxValue();
		} else if (board.isStalemate()) {
			return 0;
		} else if (depth == 0 || alpha >= beta) {
			return evaluate(board, eval);
		} else {
			int score = evalMoves(board, eval, depth, alpha, beta).getScore();
			alpha = (score > alpha) ? score : alpha;
			return score;
		}
	}
	
	private MoveScore singularExtension(Chessboard board, BoardEval eval){
		MoveScore best = null;
		Chessboard bestBoard = null;
		ArrayList<Integer> scores = new ArrayList<Integer>();
		for (Move m: board.getLegalMoves()) {
			Chessboard next = generate(board, m);
			MoveScore result = new MoveScore(-evalBoard(next, eval, 0, 0, 0), m);
			scores.add(result.getScore());
			if (best == null || result.getScore() > best.getScore()) {
				best = result;
				bestBoard = next;
			}
		}
		
		/*
		if(DEBUG) {
			System.out.println("BEST: \t\t" + best);
			System.out.println("BESTBOARD: \t" + bestBoard + "\n");
		}
		*/
		
		if(best != null && best.getScore() > outlierScore(scores)){
			MoveScore ms = singularExtension(bestBoard, eval);
			if(ms != null){
				int s = ms.getScore();
				return new MoveScore(-s, bestBoard.getLastMove());
			}
		}
		return best;
	}
	
	private int outlierScore(ArrayList<Integer> scores){
		scores.sort((s1, s2) -> s1.compareTo(s2));
		int temp = scores.size() / 4;
		int median = temp*2;
		int q1 = median - temp;
		int q3 = median + temp;
		int qRange = scores.get(q3) - scores.get(q1);
		return (int) (scores.get(q3) + qRange * 1.5);
	}
	
	private List<Move> reorder(List<Move> movesList){
		ArrayList<Move> moves = new ArrayList<Move>(movesList);
		ArrayList<Move> results = new ArrayList<Move>();
		results.addAll(moves.stream().filter((m) -> m.canCauseCheck()).collect(Collectors.toList()));
		moves.removeIf((m) -> m.canCauseCheck());
		if(DEBUG) {
			System.out.print("Input: " + moves.size());
			System.out.println("\tOutput: " + results.size());
		}
		results.addAll(moves.stream().filter((m) -> m.promotes()).collect(Collectors.toList()));
		moves.removeIf((m) -> m.promotes());
		if(DEBUG) {
			System.out.print("Input: " + moves.size());
			System.out.println("\tOutput: " + results.size());
		}
		results.addAll(moves.stream().filter((m) -> m.captures()).collect(Collectors.toList()));
		moves.removeIf((m) -> m.captures());
		if(DEBUG) {
			System.out.print("Input: " + moves.size());
			System.out.println("\tOutput: " + results.size());
		}
		results.addAll(moves);
		if(DEBUG) {
			System.out.print("Input: " + moves.size());
			System.out.println("\tOutput: " + results.size() + "\n");
		}
		return results;
	}
}