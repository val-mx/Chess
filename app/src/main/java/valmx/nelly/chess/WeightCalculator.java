package valmx.nelly.chess;

import android.util.Log;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import valmx.nelly.chess.figures.Bishop;
import valmx.nelly.chess.figures.Figure;
import valmx.nelly.chess.figures.Horse;
import valmx.nelly.chess.figures.King;
import valmx.nelly.chess.figures.MoveInfo;
import valmx.nelly.chess.figures.Pawn;
import valmx.nelly.chess.figures.Queen;
import valmx.nelly.chess.figures.Rook;

public class WeightCalculator {

    public static MoveInfo getBestPossibleMove(Figure[][] pieces, int depth, MoveInfo lastMove) {

        if (depth == 3) return null;

        Figure[][] board = copyArray(pieces);

        LinkedList<MoveInfo> allPossibleMoves = getAllPossibleMoves(board, 0);

        allPossibleMoves.forEach(m -> {

            if(m.getAction() == MoveInfo.Action.ROCHADE_LEFT ||m.getAction() == MoveInfo.Action.ROCHADE_RIGHT) return;
            if(m.getAction() == MoveInfo.Action.ENPASSANT) return;
            if(m.getAction() == MoveInfo.Action.POSSIBLEPAWNCAPTURE) return;

            Figure[][] recursionBoard = copyArray(pieces);

            Figure fig = m.getActor();
            int y = fig.getY();
            int x = fig.getX();
            recursionBoard[x][y] = null;
//            Log.i("INFO",m.toString());

            fig.setX(m.getX());
            fig.setY(m.getY());

            if(m.getX() == 8)
                recursionBoard[m.getX()-1][m.getY()-1] = fig.copy();

            else
            if(m.getX() == -1)
            recursionBoard[m.getX()+1][m.getY()+1] = fig.copy();

            else recursionBoard[m.getX()][m.getY()] = fig.copy();

            m.setWorth(getWortSum(recursionBoard));
            fig.setX(x);
            fig.setY(y);


        });

        MoveInfo maxMoveInfo = null;


        for (MoveInfo m : allPossibleMoves) {

                Figure[][] recursionBoard = copyArray(pieces);
                Figure a = m.getActor().copy();


                recursionBoard[a.getX()][a.getY()] = null;
                recursionBoard[m.getX()][m.getY()] = a;
                a.setX(m.getX());
                a.setY(m.getY());


            MoveInfo bestPossibleMove = getBestPossibleMove(recursionBoard, depth + 1,m);
            if(bestPossibleMove != null) {
                m.setWorth(m.getWorth() + bestPossibleMove.getWorth());
                m.chachedWoth = bestPossibleMove.getWorth();
            }

        }

        for (MoveInfo m : allPossibleMoves) {
            if(maxMoveInfo == null) maxMoveInfo =m;

            else  {

                if(depth%2 == 0) {
                    if(m.getWorth()<=maxMoveInfo.getWorth()) maxMoveInfo = m;

                } else {
                    if(m.getWorth()>maxMoveInfo.getWorth()) maxMoveInfo = m;

                }

            }

        }










        /*{
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 3, 3, 3, 0, 0},
                {0, 0, 0, 4, 4, 4, 0, 0},
                {0, 0, 0, 4, 4, 4, 0, 0},
                {0, 0, 0, 3, 3, 3, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},

        };*/

/*
        LinkedList<MoveInfo> attacker = getAllPossibleMoves(pieces, team);
        LinkedList<MoveInfo> attacked = new LinkedList<>();

        if (team == 1) {
            attacked.addAll(getAllPossibleMoves(pieces, 0));
        } else
            attacked.addAll(getAllPossibleMoves(pieces, 1));

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                AtomicInteger weightSum = new AtomicInteger();

                int finalY = y;
                int finalX = x;
                attacked.forEach(r -> {
                    if (r.getX() == finalX && r.getY() == finalY) {

                        if (r.getAction() != MoveInfo.Action.PAWNMOVE)
                            weightSum.addAndGet(-3);
                    }
                });

                attacker.forEach(r -> {
                    int weightToSet = 0;
                    if (r.getX() == finalX && r.getY() == finalY) {
                        if (r.getAction() == MoveInfo.Action.MOVE)
                            weightToSet += 1;
                        else if (r.getAction() == MoveInfo.Action.CAPTURE)
                            weightToSet += getWorth(pieces[r.getX()][r.getY()]);
                        else if (r.getAction() == MoveInfo.Action.POSSIBLEPAWNCAPTURE)
                            weightToSet += 3;

                    }
                    weightSum.addAndGet(weightToSet);
                });
                weights[x][y] += weightSum.get();
            }
        }
*/
//        if(lastMove != null && maxMoveInfo != null)
//        if(maxMoveInfo.getActor().getX() == lastMove.getX() && maxMoveInfo.getActor().getY() == lastMove.getY())
//            return null;

        return maxMoveInfo;
    }


    public static Figure[][] copyArray(Figure[][] figs) {

        Figure[][] figs1 = new Figure[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                Figure fig = figs[i][j];
                if (fig == null) continue;

                figs1[i][j] = fig.copy();

            }
        }

        return figs1;
    }

    public static int getWortSum(Figure[][] figs) {
        int sum = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                sum += getWorth(figs[x][y]);

            }
        }
        return sum;
    }

    private int getPosWorth(MoveInfo i, Figure[][] figs) {
        return 0;

    }

    public static LinkedList<MoveInfo> getAllPossibleMoves(Figure[][] pieces, int team) {
        LinkedList<MoveInfo> result = new LinkedList<>();
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces.length; j++) {
                Figure figure = pieces[i][j];
                if (figure != null)
                    if (figure.getTeam() == team) result.addAll(figure.getPossibleMoves(pieces));
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
        return (int) (returnValue * -1.5);
    }
}
