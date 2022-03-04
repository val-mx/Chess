package valmx.nelly.chess;

import java.util.LinkedList;

import valmx.nelly.chess.figures.Bishop;
import valmx.nelly.chess.figures.Figure;
import valmx.nelly.chess.figures.King;
import valmx.nelly.chess.figures.Knight;
import valmx.nelly.chess.figures.MoveInfo;
import valmx.nelly.chess.figures.Pawn;

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

            i.setWorth(max(b, depth - 1).getWorth() + getMoveExtraWorth(i, b));

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

            i.setWorth(min(b, depth - 1).getWorth() + getMoveExtraWorth(i, b) * -1);

            b.undoLastAction();

            // Replacing best move if this one is better

            if (bestMove == null || i.getWorth() < bestMove.getWorth()) bestMove = i;

        }

        return bestMove;
    }

    private int getMoveExtraWorth(MoveInfo i, ChessBoard board) {


        final Figure actor = i.getActor();
        int sum = 0;

        if (i.getAction().toString().contains("ROCHADE")) sum += 5;
        else if (actor instanceof King/* && actor.getLastMove() == -1*/) sum += -90;


        if ((actor instanceof Pawn && actor.getX() == 3) && i.getY() == 3) {
            if (board.getRound() == 0) sum += 1000;
        }

        if (i.getY() == 3 && i.getX() == 3) sum += 5;
        if (i.getY() == 3 && i.getX() == 4) sum += 5;
        if (i.getY() == 4 && i.getX() == 3) sum += 5;
        if (i.getY() == 4 && i.getX() == 4) sum += 5;


        if (actor.getLastMove() == board.getRound()) sum += -5;

        if (actor instanceof Knight || actor instanceof Bishop)
            if (actor.getLastMove() == -1 && board.getRound() < 10) sum += 5;

        return sum;
    }

}
