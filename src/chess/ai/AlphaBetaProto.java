package chess.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import chess.core.Chessboard;
import chess.core.Move;

public class AlphaBetaProto extends Searcher {
	private int alpha;
	private int beta;
	private final boolean DEBUG = false;
	
	@Override
	public MoveScore findBestMove(Chessboard board, BoardEval eval, int depth) {
		setup(board, eval, depth);
		alpha = Integer.MIN_VALUE;
		beta = Integer.MAX_VALUE;
		MoveScore result = evalMoves(board, eval, depth, true);
		tearDown();
		return result;
	}
	
	MoveScore evalMoves(Chessboard board, BoardEval eval, int depth, boolean isMax) {
		MoveScore best = null;
		for (Move m: reorder(board.getLegalMoves())) {
			Chessboard next = generate(board, m);
			MoveScore result = new MoveScore(-evalBoard(next, eval, depth - 1, !isMax), m);
			if (best == null || result.getScore() > best.getScore()) {
				best = result;
			}
		}
		return best;
	}	
	
	int evalBoard(Chessboard board, BoardEval eval, int depth, boolean isMax) {
		if (!board.hasKing(board.getMoverColor()) || board.isCheckmate()) {
			return -eval.maxValue();
		} else if (board.isStalemate()) {
			return 0;
		} else if (depth == 0 || alpha > beta) {
			return evaluate(board, eval);
		} else {
			int score = evalMoves(board, eval, depth, isMax).getScore();
			if(DEBUG) {System.out.print("isMax: " + isMax + "\t");}
			if( isMax ){
				if(DEBUG && (score > alpha)) {System.out.println("alpha: " + alpha + " -> " + score);}
				alpha = (score > alpha) ? score : alpha;
			}
			else{
				if(DEBUG && (score < beta)) {System.out.println("beta: " + beta + " -> " + score);}
				beta = (score < beta) ? score : beta;
			}
			return score;
		}
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