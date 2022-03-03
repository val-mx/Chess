package valmx.nelly.chess;

import java.util.LinkedList;

import valmx.nelly.chess.figures.Figure;
import valmx.nelly.chess.figures.King;
import valmx.nelly.chess.figures.MoveInfo;

public class MiniMax {

    public MoveInfo min(ChessBoard b, int depth) {

        // Returning end worth when depth 0 is reached

        if (depth == 0) {
            final MoveInfo moveInfo = new MoveInfo();

            moveInfo.setWorth(WeightCalculator.getWortSum(b.getBoard()));

            return moveInfo;
        }

        final LinkedList<MoveInfo> allPossibleMoves = WeightCalculator.getAllPossibleMoves(b, true);

        // Searching for move with minimal result (best for white)

        MoveInfo bestMove = null;

        for (MoveInfo i : allPossibleMoves) {

            b.doAction(i);

            i.setWorth(max(b, depth - 1).getWorth() + getMoveExtraWorth(i,b));

            b.undoLastAction();

            // Replacing best move if this one is better

            if (bestMove == null || i.getWorth() > bestMove.getWorth()) bestMove = i;

        }

        return bestMove;
    }

    public MoveInfo max(ChessBoard b, int depth) {

        // Returning end worth when depth 0 is reached

        if (depth == 0) {
            final MoveInfo moveInfo = new MoveInfo();

            moveInfo.setWorth(WeightCalculator.getWortSum(b.getBoard()));

            return moveInfo;
        }

        final LinkedList<MoveInfo> allPossibleMoves = WeightCalculator.getAllPossibleMoves(b, false);

        // Searching for move with minimal result (best for white)

        MoveInfo bestMove = null;

        for (MoveInfo i : allPossibleMoves) {

            b.doAction(i);

            i.setWorth(min(b, depth - 1).getWorth() + getMoveExtraWorth(i,b) * -1);

            b.undoLastAction();

            // Replacing best move if this one is better

            if (bestMove == null || i.getWorth() < bestMove.getWorth()) bestMove = i;

        }

        return bestMove;
    }

    private int getMoveExtraWorth(MoveInfo i, ChessBoard board) {
        if (i.getAction().toString().contains("ROCHADE")) return 200;
        if (i.getAction() == MoveInfo.Action.ENPASSANT) return 10000;
        if (i.getAction() == MoveInfo.Action.CAPTURE) return 20;

        final Figure actor = i.getActor();

        if (actor instanceof King && actor.getLastMove() == -1) return -90;
        if (actor.getLastMove() == board.getRound()) return -70;

        if(actor.getLastMove() == -1 && board.getRound() > 10) return 90;

        return 0;
    }

}
