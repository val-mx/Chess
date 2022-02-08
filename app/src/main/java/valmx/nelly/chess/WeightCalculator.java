package valmx.nelly.chess;

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

    public static int[][] getWeights(Figure[][] pieces, int team) {
        int[][] weights = {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 3, 3, 3, 0, 0},
                {10, 0, 0, 4, 4, 4, 0, 10},
                {10, 0, 0, 4, 4, 4, 0, 10},
                {0, 0, 0, 3, 3, 3, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},

        };

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

        return weights;
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
        if (f instanceof Pawn) return 1;
        if (f instanceof Queen) return 4;
        if (f instanceof Rook) return 3;
        if (f instanceof King) return 8;
        if (f instanceof Bishop) return 2;
        if (f instanceof Horse) return 3;
        return 1000;
    }
}
