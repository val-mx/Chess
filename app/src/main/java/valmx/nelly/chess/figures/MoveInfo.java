package valmx.nelly.chess.figures;

import valmx.nelly.chess.figures.Figure;

public class MoveInfo {

    private final int x;
    private final int y;
    private int fromX = 0;
    private int fromY = 0;
    private Action action = Action.MOVE;
    private Figure actor;
    public int chachedWoth = 0;
    int worth = 0;

    public MoveInfo(int x, int y, Action action, Figure actor) {
        this.x = x;
        this.y = y;
        this.action = action;
        this.actor = actor;
        if (actor != null) {
            fromX = actor.getX();
            fromY = actor.getY();
        }
    }

    public MoveInfo() {
        x = 0;
        y = 0;
    }

    public void invertWorth() {
        worth *= -1;
    }

    public int getFromX() {
        return fromX;
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

    public int getFromY() {
        return fromY;
    }

    public void setActor(Figure actor) {
        this.actor = actor;
        fromX = actor.getX();
        fromY = actor.getY();
    }

    public int getY() {
        return y;
    }

    public Figure getActor() {
        return actor;
    }

    public enum Action {
        MOVE, CAPTURE, PAWNMOVE, POSSIBLEPAWNCAPTURE, ROCHADE_LEFT, ROCHADE_RIGHT, ENPASSANT, PAWNMOVE_DOUBLE
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
