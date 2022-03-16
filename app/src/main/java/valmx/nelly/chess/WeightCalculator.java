package valmx.nelly.chess;

import android.os.AsyncTask;

import java.util.LinkedList;

import valmx.nelly.chess.figures.Bishop;
import valmx.nelly.chess.figures.Figure;
import valmx.nelly.chess.figures.Knight;
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

    // https://web.archive.org/web/20180115224109/https://chessprogramming.wikispaces.com/Simplified+evaluation+function

    private static int[][] bishopValues = {
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 10, 10, 5, 0, -10},
            {-10, 5, 5, 10, 10, 5, 5, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 10, 10, 10, 10, 10, 10, -10},
            {-10, 5, 0, 0, 0, 0, 5, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}
    };

    private static int[][] pawnValues = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5, 5, 10, 25, 25, 10, 5, 5},
            {0, 0, 0, 20, 20, 0, 0, 0},
            {5, -5, -10, 0, 0, -10, -5, 5},
            {5, 10, 10, -20, -20, 10, 10, 5},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };

    private static int[][] knightValues = {
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 0, 0, 0, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 5, -30},
            {-30, 0, 15, 20, 20, 15, 0, -30},
            {-30, 5, 10, 15, 15, 10, 5, -30},
            {-40, -20, 0, 5, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}
    };

    private static int[][] rookValues = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {5, 10, 10, 10, 10, 10, 10, 5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {-5, 0, 0, 0, 0, 0, 0, -5},
            {0, 0, 0, 5, 5, 0, 0, 0}
    };

    private static int[][] queenValues = {
            {-20, -10, -10, -5, -5, -10, -10, -20},
            {-10, 0, 0, 0, 0, 0, 0, -10},
            {-10, 0, 5, 5, 5, 5, 0, -10},
            {-5, 0, 5, 5, 5, 5, 0, -5},
            {0, 0, 5, 5, 5, 5, 0, -5},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-20, -10, -10, -5, -5, -10, -10, -20}
    };


    public static MoveInfo getBestPossibleMove(Figure[][] pieces, ChessView.ResultRunnable r, boolean player) {

        AsyncTask<Void, Void, MoveInfo> task = new AsyncTask<Void, Void, MoveInfo>() {
            @Override
            protected MoveInfo doInBackground(Void... voids) {
//                return minMax(pieces, 5, true);
                if (player)
                    return new MiniMax().min(new ChessBoard(copyArray(pieces)), 4, 0);
                else
                    return new MiniMax().max(new ChessBoard(copyArray(pieces)), 4, 0);
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
            LinkedList<MoveInfo> allPossibleMoves = sortList(getAllPossibleMoves(board, true));

            for (MoveInfo i : allPossibleMoves) {

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
            LinkedList<MoveInfo> allPossibleMoves = sortList(getAllPossibleMoves(board, false));

            for (MoveInfo i : allPossibleMoves) {

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


    private static LinkedList<MoveInfo> sortList(LinkedList<MoveInfo> info) {

        LinkedList<MoveInfo> result = new LinkedList<>();

        for (MoveInfo i : info) {

            if (i.getAction() == MoveInfo.Action.CAPTURE) {
                result.addFirst(i);
            } else
                result.addLast(i);

        }

        return result;

    }

    public static int getWortSum(Figure[][] figs) {
        int sum = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                sum += getWorth(figs[x][y]);
//                if(figure != null)
//                sum+= getPosWorth(figure);
            }
        }
        return sum;
    }

    public static LinkedList<MoveInfo> getAllPossibleMoves(ChessBoard board, boolean team) {

        boolean lastPlayer = board.getPlayer();
        board.setPlayer(team);

        final LinkedList<MoveInfo> legalCheckMoves = board.getAllPossibleMoves();

        board.setPlayer(lastPlayer);

        return legalCheckMoves;
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

        if (f instanceof Pawn) returnValue = 10;
        if (f instanceof Queen) returnValue = 85;
        if (f instanceof Rook) returnValue = 50;
        if (f instanceof King) returnValue = (int) Math.pow(10, 5);
        if (f instanceof Bishop) returnValue = 30;
        if (f instanceof Knight) returnValue = 30;
        if (f.getPlayer())
            return returnValue;
        return returnValue * -1;
    }

    public static int getPosWorth(Figure f) {
        int returnValue = 0;
        int x = f.getX();
        int y = f.getY();

        if (f.getPlayer()) {
            y = 7 - y;
        }

        if (f instanceof Rook) {
            returnValue = rookValues[x][y];
        } else if (f instanceof Pawn) {
            returnValue = pawnValues[x][y];
        } else if (f instanceof Knight) {
            returnValue = knightValues[x][y];
        } else if (f instanceof Bishop) {
            returnValue = bishopValues[x][y];
        }
        if (f instanceof Queen) {
            returnValue = queenValues[x][y];
        }

        if (f.getPlayer())
            return returnValue;
        return returnValue * -1;
    }
}
