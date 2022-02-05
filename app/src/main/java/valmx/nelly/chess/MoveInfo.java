package valmx.nelly.chess;

public class MoveInfo {

    private final int x;
    private final int y;
    private final Action action;

    public enum Action {
        MOVE, CAPTURE
    }

    public MoveInfo(int x, int y, Action action) {
        this.x = x;
        this.y = y;
        this.action = action;
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
}
