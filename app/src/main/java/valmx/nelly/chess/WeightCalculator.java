package valmx.nelly.chess;

import android.os.AsyncTask;

import java.util.LinkedList;

import valmx.nelly.chess.figures.Bishop;
import valmx.nelly.chess.figures.Figure;
import valmx.nelly.chess.figures.Horse;
import valmx.nelly.chess.figures.King;
import valmx.nelly.chess.figures.MoveInfo;
import valmx.nelly.chess.figures.Pawn;
import valmx.nelly.chess.figures.Queen;
import valmx.nelly.chess.figures.Rook;

public class WeightCalculator {

    private static final int[][] baseWeights = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 90, 0, 0, 0, 0, 0},
            {0, 0, 120, 0, 0, 120, 0, 0},
            {0, 0, 90, 0, 0, 90, 0, 0},
            {0, 0, 90, 0, 0, 90, 0, 0},
            {0, 0, 120, 0, 0, 120, 0, 0},
            {0, 0, 90, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };


    public static MoveInfo getBestPossibleMove(Figure[][] pieces, ChessView.ResultRunnable r) {

        AsyncTask<Void, Void, MoveInfo> task = new AsyncTask<Void, Void, MoveInfo>() {
            @Override
            protected MoveInfo doInBackground(Void... voids) {
//                return minMax(pieces, 5, true);
                return minMax(new ChessBoard(copyArray(pieces)), 5, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
            }

            @Override
            protected void onPostExecute(MoveInfo moveInfo) {
                r.run(moveInfo);
            }
        };
        task.execute();

        return null;

    }

    public static MoveInfo minMax(ChessBoard board, int depth, int alpha, int beta, boolean player) {


        if (depth == 0) {

            MoveInfo i = new MoveInfo(1, 1, MoveInfo.Action.MOVE, null);
            i.setWorth(getWortSum(board.getBoard()));
            return i;
        }
        if (player) {

            int maxEval = -Integer.MAX_VALUE;
            MoveInfo bestMove = null;
            LinkedList<MoveInfo> allPossibleMoves = getAllPossibleMoves(board, true);

            for (MoveInfo i : allPossibleMoves) {

                if (i.getAction() == MoveInfo.Action.ROCHADE_LEFT || i.getAction() == MoveInfo.Action.ROCHADE_RIGHT)
                    continue;

                board.doAction(i);

                i.setWorth(minMax(board, depth - 1, alpha, beta, false).getWorth());

                board.undoLastAction();

                if (i.getWorth() > maxEval) {
                    maxEval = i.getWorth();
                    bestMove = i;
                }
                if (i.getWorth() > alpha) alpha = i.getWorth();

                if (beta <= alpha) return bestMove;
            }
            return bestMove;
        } else {
            int minEval = Integer.MAX_VALUE;
            MoveInfo bestMove = null;
            LinkedList<MoveInfo> allPossibleMoves = getAllPossibleMoves(board, false);

            for (MoveInfo i : allPossibleMoves) {
                if (i.getAction() == MoveInfo.Action.ROCHADE_LEFT || i.getAction() == MoveInfo.Action.ROCHADE_RIGHT)
                    continue;

                board.doAction(i);
                i.setWorth(minMax(board, depth - 1, alpha, beta, true).getWorth());
                board.undoLastAction();

                if (i.getWorth() < minEval) {
                    minEval = i.getWorth();
                    bestMove = i;
                }

                if (i.getWorth() < beta) beta = i.getWorth();

                if (beta <= alpha) return bestMove;

            }
            return bestMove;
        }
    }

    public static int getWortSum(Figure[][] figs) {
        int sum = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                sum += getWorth(figs[x][y]);
                Figure figure = figs[x][y];

                if (figure != null) {
                    if (figure.getPlayer()) sum += baseWeights[x][y];
                    else sum -= baseWeights[x][y];
                }
            }
        }
        return sum;
    }

    public static LinkedList<MoveInfo> getAllPossibleMoves(ChessBoard board, boolean team) {
        board.setPlayer(team);
        return board.getLegalCheckMoves();
    }

    public static Figure[][] copyArray(Figure[][] arrayToCopy) {

        Figure[][] result = new Figure[8][8];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                final Figure figure = arrayToCopy[x][y];
                if (figure != null)
                    result[x][y] = figure.copy();
            }
        }

        return result;
    }

    public static int getWorth(Figure f) {

        if (f == null) return 0;

        int returnValue = 0;

        if (f instanceof Pawn) returnValue = 100;
        if (f instanceof Queen) returnValue = 800;
        if (f instanceof Rook) returnValue = 500;
        if (f instanceof King) returnValue = (int) Math.pow(10, 5);
        if (f instanceof Bishop) returnValue = 325;
        if (f instanceof Horse) returnValue = 275;
        if (f.getPlayer())
            return returnValue;
        return returnValue * -1;
    }
}
