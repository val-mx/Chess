package valmx.nelly.chess;

import java.util.LinkedList;

import valmx.nelly.chess.figures.Bishop;
import valmx.nelly.chess.figures.Figure;
import valmx.nelly.chess.figures.King;
import valmx.nelly.chess.figures.Knight;
import valmx.nelly.chess.figures.MoveInfo;
import valmx.nelly.chess.figures.Pawn;

public class MiniMax {

    /**
     * @param b
     * @param depth
     * @param extraSum wert der extra Bewertungsfunktion, also wert ohne beachtung des Materials
     * @return
     */

    public MoveInfo max(ChessBoard b, int depth, int extraSum, int alpha, int beta) {

        // Returning end worth when depth 0 is reached
        b.setPlayer(true);
        if (depth == 0) {
            final MoveInfo moveInfo = new MoveInfo();

            extraSum -= getStellungExtraWorth(b, false);
            extraSum += getStellungExtraWorth(b, true);

            moveInfo.setWorth(WeightCalculator.getWortSum(b.getBoard()) + extraSum);

            return moveInfo;
        }

        final LinkedList<MoveInfo> allPossibleMoves = WeightCalculator.getAllPossibleMoves(b, true);

        // Searching for move with minimal result (best for white)

        MoveInfo bestMove = null;

        for (MoveInfo i : allPossibleMoves) {

            b.doAction(i);

            i.setWorth(min(b, depth - 1, extraSum -= getMoveExtraWorth(i, b), alpha, beta).getWorth());

            b.undoLastAction();

            // Replacing best move if this one is better


            if (bestMove == null || i.getWorth() > bestMove.getWorth()) bestMove = i;

            // Pruning

            if (bestMove.getWorth() <= alpha) break;

            if (beta > bestMove.getWorth()) beta = bestMove.getWorth();

        }

        return bestMove;
    }

    public MoveInfo min(ChessBoard b, int depth, int extraSum, int alpha, int beta) {

        // Returning end worth when depth 0 is reached

        b.setPlayer(false);

        if (depth == 0) {
            final MoveInfo moveInfo = new MoveInfo();
            extraSum -= getStellungExtraWorth(b, false);
            extraSum += getStellungExtraWorth(b, true);
            moveInfo.setWorth(WeightCalculator.getWortSum(b.getBoard()) + extraSum);

            return moveInfo;
        }


        final LinkedList<MoveInfo> allPossibleMoves = WeightCalculator.getAllPossibleMoves(b, false);

        // Searching for move with minimal result (best for white)

        MoveInfo bestMove = null;

        for (MoveInfo i : allPossibleMoves) {

            b.doAction(i);

            i.setWorth(max(b, depth - 1, extraSum += getMoveExtraWorth(i, b), alpha, beta).getWorth());

            b.undoLastAction();

            // Replacing best move if this one is better

            if (bestMove == null || i.getWorth() < bestMove.getWorth()) bestMove = i;

            if (bestMove.getWorth() >= beta) break;

            if (alpha < bestMove.getWorth()) alpha = bestMove.getWorth();


        }

        return bestMove;
    }

    private int getMoveExtraWorth(MoveInfo i, ChessBoard board) {


        final Figure actor = i.getActor();
        int sum = 0;

        if (i.getAction().toString().contains("ROCHADE")) sum += 5;
        else if (actor instanceof King/* && actor.getLastMove() == -1*/) sum += -2;


//        if (board.isKingInCheck()) sum += 20;


//        if ((actor instanceof Pawn && actor.getX() == 3) && i.getY() == 3) {
//            if (board.getRound() < 3) sum += 1000;
//        }



        return 0;
    }

    private int getStellungExtraWorth(ChessBoard b, boolean player) {

        int sum = 0;

        if (isFigureAtPos(Pawn.class, 3, 3, b, player)) sum += 5;
        if (isFigureAtPos(Pawn.class, 4, 3, b, player)) sum += 5;
        if (isFigureAtPos(Pawn.class, 3, 4, b, player)) sum += 5;
        if (isFigureAtPos(Pawn.class, 4, 4, b, player)) sum += 5;

        final LinkedList<Figure> figures = b.getFigures(player);

        boolean isKingAlive = false;

        for (Figure f : figures) {

            if (f instanceof King) isKingAlive = true;

            if (f instanceof Bishop || f instanceof Knight) {

                if (f.getLastMove() == -1) {
                    sum += 5;
                }

            }


//            if (f.getLastMove() == f.getLastMoveCache() - 1) {
//                sum -= 15;
//            }

        }

/*        if (!isKingAlive) {
            sum -= 10000;
        }*/

        return 0;


    }

    private boolean isFigureAtPos(Class<?> c, int x, int y, ChessBoard b, boolean player) {

        final Figure figure = b.getFigure(x, y);

        return ((figure != null && figure.getClass() == c) && figure.getPlayer() == player);
    }

}
