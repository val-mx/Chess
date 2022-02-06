package valmx.nelly.chess.figures;

import java.util.LinkedList;

public class King extends Figure {
    public King(int team, int x, int y) {
        super(team, x, y);
    }

    @Override
    public LinkedList<MoveInfo> getPossibleMoves(Figure[][] field) {

        LinkedList<MoveInfo> enemyFigMoves = new LinkedList<>();
        LinkedList<MoveInfo> forbiddenMoves = new LinkedList<>();

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                Figure fig = field[i][j];
                if (fig != null && fig.team != team) {

                    if (fig instanceof King) continue;

                    enemyFigMoves.addAll(fig.getPossibleMoves(field));
                }
            }
        }
        LinkedList<MoveInfo> info = new LinkedList<>();
        int testX, testY;
        for (int i = 0; i < 3; i++) {
            testX = x - 1 + i;
            for (int j = 0; j < 3; j++) {
                testY = y - 1 + j;

                if (testX < 0 || testY < 0) continue;
                if (testX > 7 || testY > 7) continue;
                if (testX == x && testY == y) continue;

                Figure fig = field[testX][testY];

                // Validify pos

                boolean isValidPos = true;


                for (MoveInfo inf : enemyFigMoves) {

                    if (inf.getAction() == MoveInfo.Action.PAWNMOVE) continue;

                    if (inf.getX() == testX && inf.getY() == testY) {
                        isValidPos = false;
                        forbiddenMoves.add(inf);
                    }
                }

                if (!isValidPos) continue;

                if (fig == null) {
                    info.add(new MoveInfo(testX, testY, MoveInfo.Action.MOVE, this));
                } else if (fig.team != team) {
                    info.add(new MoveInfo(testX, testY, MoveInfo.Action.CAPTURE, this));
                }
            }
        }

        for (MoveInfo inf : info) {
            for (MoveInfo forbiddenInf : forbiddenMoves) {
                if (inf.getX() == forbiddenInf.getX() && inf.getY() == forbiddenInf.getY()) {
                    info.remove(inf);
                }
            }

        }
        ;

        return info;
    }
}
