package chess.ai;

import chess.core.Chessboard;
import chess.core.Move;

public class AlphaBetaProto extends Searcher {
	private int alpha;
	private int beta;
	private final boolean DEBUG = true;
	
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
		for (Move m: board.getLegalMoves()) {
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
}