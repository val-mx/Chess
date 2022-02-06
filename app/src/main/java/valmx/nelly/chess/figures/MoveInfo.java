package valmx.nelly.chess.figures;

import valmx.nelly.chess.figures.Figure;

public class MoveInfo {

    private final int x;
    private final int y;
    private final Action action;
    private Figure actor;

    public enum Action {
        MOVE, CAPTURE, PAWNMOVE, POSSIBLEPAWNCAPTURE
    }

    public MoveInfo(int x, int y, Action action, Figure actor) {
        this.x = x;
        this.y = y;
        this.action = action;
        this.actor = actor;
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
}
