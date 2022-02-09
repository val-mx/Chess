package valmx.nelly.chess.figures;

import valmx.nelly.chess.figures.Figure;

public class MoveInfo {

    private final int x;
    private final int y;
    private final Action action;
    private Figure actor;
    public int chachedWoth = 0;
    int worth = 0;

    public enum Action {
        MOVE, CAPTURE, PAWNMOVE, POSSIBLEPAWNCAPTURE, ROCHADE_LEFT, ROCHADE_RIGHT, ENPASSANT, PAWNMOVE_DOUBLE;
    }

    public MoveInfo(int x, int y, Action action, Figure actor) {
        this.x = x;
        this.y = y;
        this.action = action;
        this.actor = actor;
    }

    public int getWorth() {
        return worth;
    }

    public void setWorth(int worth) {
        this.worth = worth;
    }

    public Action getAction() {
        return action;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Figure getActor() {
        return actor;
    }

    public void setActor(Figure actor) {
        this.actor = actor;
    }

    @Override
    public String toString() {
        return "MoveInfo{" +
                "x=" + x +
                ", y=" + y +
                ", action=" + action +
                ", actor=" + actor +
                ", worth=" + worth +
                '}';
    }
}
