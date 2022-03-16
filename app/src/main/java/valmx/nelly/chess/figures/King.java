package valmx.nelly.chess.figures;

import java.util.LinkedList;

public class King extends Figure {

    public King(boolean team, int x, int y) {
        super(team, x, y);
    }

    @Override
    public LinkedList<MoveInfo> getPossibleMoves(Figure[][] field) {

        LinkedList<MoveInfo> enemyFigMoves = new LinkedList<>();

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


                if (!isValidPos) continue;

                if (fig == null) {
                    info.add(new MoveInfo(testX, testY, MoveInfo.Action.MOVE, this));
                } else if (fig.team != team) {
                    info.add(new MoveInfo(testX, testY, MoveInfo.Action.CAPTURE, this));
                }
            }
        }

            if (lastMove == -1 && x == 3) {
                for (int i = 1; i < 19; i++) {

                    testX = x - i;

                    if(testX< 0) break;
                    Figure fig = field[testX][y];
                    if (fig != null && testX != 0) {
                        break;
                    } else if (fig instanceof Rook) {
                        if (fig.lastMove == -1) {
                            MoveInfo inf = new MoveInfo(x - 2, y, MoveInfo.Action.ROCHADE_LEFT, this);
                            info.add(inf);
                            break;
                        }
                    }

                    if (testX == 0) break;

                }

                for (int i = 1; i < 19; i++) {

                    testX = x + i;


                    Figure fig = field[testX][y];
                    if (fig != null && testX != 7) {
                        break;
                    } else if (fig instanceof Rook) {
                        if (fig.lastMove == -1) {
                            MoveInfo inf = new MoveInfo(x + 2, y, MoveInfo.Action.ROCHADE_RIGHT, this);
                            info.add(inf);
                            break;
                        }
                    }

                    if (testX == 7) break;

                }
            }

        return info;
    }

    @Override
    public Figure copy() {
        return new King(team,x,y);
    }
}
