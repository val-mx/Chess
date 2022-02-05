package valmx.nelly.chess;

import java.util.LinkedList;

public abstract class Figure {
    private int team;
    private int x;
    private int y;

    public Figure(int team,int x, int y) {
        this.team = team;
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public void setX(int x) {
        this.x = x;
    }

    abstract public LinkedList<MoveInfo> getPossibleMoves(Figure[][] field);

}
