package chess.ai;

import chess.core.Chessboard;
import chess.core.Move;

public class AlphaBeta extends Searcher {
	private int alpha;
	private int beta;
	@Override
	public MoveScore findBestMove(Chessboard board, BoardEval eval, int depth) {
		setup(board, eval, depth);
		alpha = Integer.MIN_VALUE;
		beta = Integer.MAX_VALUE;
		boolean isMax = true;
		MoveScore result = evalMoves(board, eval, depth, isMax);
		tearDown();
		return result;
	}
	
	MoveScore evalMoves(Chessboard board, BoardEval eval, int depth, boolean isMax) {
		MoveScore best = null;
		for (Move m: board.getLegalMoves()) {
			Chessboard next = generate(board, m);
			MoveScore result = new MoveScore(-evalBoard(next, eval, depth - 1, !isMax), m);//swap and negate alpha, beta here
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
		} else if (depth == 0 || alpha >= beta) {
			return evaluate(board, eval);
		} else {
			int score = evalMoves(board, eval, depth, isMax).getScore();
			if( isMax ){
				alpha = score;
			}
			else{
				beta = score;
			}
			return score;
		}
	}
}