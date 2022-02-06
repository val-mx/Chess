package valmx.nelly.chess.figures;

import java.util.LinkedList;

public abstract class Figure {
    protected int team;
    protected int x;
    protected int y;
    protected int lastMove = -1;

    public void setLastMove(int lastMove) {
        this.lastMove = lastMove;
    }

    public int getLastMove() {
        return lastMove;
    }

    public Figure(int team, int x, int y) {
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

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    abstract public LinkedList<MoveInfo> getPossibleMoves(Figure[][] field);

    protected void processAction(Figure[][] field, LinkedList<MoveInfo> info, Figure attacker, int x, int y) {

        if (x < 0 || y < 0) return;
        if (x > 7 || y > 7) return;


        Figure fig = field[x][y];

        if (fig == null) {
            info.add(new MoveInfo(x, y, MoveInfo.Action.MOVE,this));
        } else if (fig.team != attacker.team) {
            info.add(new MoveInfo(x, y, MoveInfo.Action.CAPTURE,this));
        }

    }

}
