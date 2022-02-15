package valmx.nelly.chess.figures;

import java.util.LinkedList;

public class Bishop extends Figure {
    public Bishop(boolean team, int x, int y) {
        super(team, x, y);
    }

    @Override
    public LinkedList<MoveInfo> getPossibleMoves(Figure[][] field) {
        LinkedList<MoveInfo> info = new LinkedList<>();

        for (int i = 1; i < 8; i++) {

            int tempX = x + i;
            int tempY = y + i;

            if (tempX > 7 || tempY > 7) break;

            Figure f = field[tempX][tempY];

            if (f == null) {
                info.add(new MoveInfo(tempX, tempY, MoveInfo.Action.MOVE, this));
            } else if (f.team == team) break;
            else {
                info.add(new MoveInfo(tempX, tempY, MoveInfo.Action.CAPTURE, this));
                break;
            }
        }
        for (int i = 1; i < 8; i++) {

            int tempX = x - i;
            int tempY = y - i;

            if (tempX < 0 || tempY <0) break;

            Figure f = field[tempX][tempY];

            if (f == null) {
                info.add(new MoveInfo(tempX, tempY, MoveInfo.Action.MOVE, this));
            } else if (f.team == team) break;
            else {
                info.add(new MoveInfo(tempX, tempY, MoveInfo.Action.CAPTURE, this));
                break;
            }
        }

        for (int i = 1; i < 8; i++) {

            int tempX = x + i;
            int tempY = y - i;

            if (tempX >7 || tempY <0) break;

            Figure f = field[tempX][tempY];

            if (f == null) {
                info.add(new MoveInfo(tempX, tempY, MoveInfo.Action.MOVE, this));
            } else if (f.team == team) break;
            else {
                info.add(new MoveInfo(tempX, tempY, MoveInfo.Action.CAPTURE, this));
                break;
            }
        }

        for (int i = 1; i < 8; i++) {

            int tempX = x - i;
            int tempY = y + i;

            if (tempY >7 || tempX <0) break;

            Figure f = field[tempX][tempY];

            if (f == null) {
                info.add(new MoveInfo(tempX, tempY, MoveInfo.Action.MOVE, this));
            } else if (f.team == team) break;
            else {
                info.add(new MoveInfo(tempX, tempY, MoveInfo.Action.CAPTURE, this));
                break;

            }
        }

        return info;
    }

    @Override
    public Figure copy() {
        return new Bishop(team,x,y);
    }
}
