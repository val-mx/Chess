package valmx.nelly.chess;

import android.os.AsyncTask;
import android.widget.Toast;

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
                return minMax(copyArray(pieces), 5, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
            }

            @Override
            protected void onPostExecute(MoveInfo moveInfo) {
                r.run(moveInfo);
            }
        };
        task.execute();

        return null;

    }

    public static MoveInfo minMax(Figure[][] pieces, int depth, int alpha, int beta, boolean player) {


        if (depth == 0) {

            MoveInfo i = new MoveInfo(1, 1, MoveInfo.Action.MOVE, null);
            i.setWorth(getWortSum(pieces));
            return i;
        }
        if (player) {

            int maxEval = -Integer.MAX_VALUE;
            MoveInfo bestMove = null;
            LinkedList<MoveInfo> allPossibleMoves = getAllPossibleMoves(pieces, 1);

            for (MoveInfo i : allPossibleMoves) {

                if (i.getAction() == MoveInfo.Action.ROCHADE_LEFT || i.getAction() == MoveInfo.Action.ROCHADE_RIGHT)
                    continue;

                final Figure actor = i.getActor();
                final int x = actor.getX();
                final int y = actor.getY();

                Figure temp = pieces[i.getX()][i.getY()];
                pieces[x][y] = null;
                pieces[i.getX()][i.getY()] = actor;
                actor.setX(i.getX());
                actor.setY(i.getY());


                i.setWorth(minMax(pieces, depth - 1, alpha, beta, false).getWorth());
                actor.setX(x);
                actor.setY(y);

                pieces[i.getX()][i.getY()] = temp;
                pieces[x][y] = actor;


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
            LinkedList<MoveInfo> allPossibleMoves = getAllPossibleMoves(pieces, 0);

            for (MoveInfo i : allPossibleMoves) {
                if (i.getAction() == MoveInfo.Action.ROCHADE_LEFT || i.getAction() == MoveInfo.Action.ROCHADE_RIGHT)
                    continue;

                final Figure actor = i.getActor();
                final int x = actor.getX();
                final int y = actor.getY();

                Figure temp = pieces[i.getX()][i.getY()];
                pieces[x][y] = null;
                pieces[i.getX()][i.getY()] = actor;

                actor.setX(i.getX());
                actor.setY(i.getY());
                i.setWorth(minMax(pieces, depth - 1, alpha, beta, true).getWorth());
                actor.setX(x);
                actor.setY(y);

                pieces[x][y] = actor;
                pieces[i.getX()][i.getY()] = temp;

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
                    if (figure.getTeam() == 1) sum += baseWeights[x][y];
                    else sum -= baseWeights[x][y];
                }
            }
        }
        return sum;
    }

    public static LinkedList<MoveInfo> getAllPossibleMoves(Figure[][] pieces, int team) {
        LinkedList<MoveInfo> result = new LinkedList<>();
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces.length; j++) {
                Figure figure = pieces[i][j];
                if (figure != null)
                    if (figure.getTeam() == team) {

                        result.addAll(figure.getPossibleMoves(pieces));

                    }
            }
        }
        return result;
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
        if (f.getTeam() == 1)
            return returnValue;
        return returnValue * -1;
    }
}
