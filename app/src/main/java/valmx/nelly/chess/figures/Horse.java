package valmx.nelly.chess.figures;

import java.util.LinkedList;

public class
Horse extends Figure {
    public Horse(int team, int x, int y) {
        super(team, x, y);
    }

    @Override
    public LinkedList<MoveInfo> getPossibleMoves(Figure[][] figures) {

        LinkedList<MoveInfo> info = new LinkedList<>();

        processAction(figures, info, this, x + 1, y - 2);
        processAction(figures, info, this, x - 1, y - 2);
        processAction(figures, info, this, x + 1, y + 2);
        processAction(figures, info, this, x - 1, y + 2);

        processAction(figures, info, this, x + 2, y - 1);
        processAction(figures, info, this, x - 2, y - 1);
        processAction(figures, info, this, x + 2, y + 1);
        processAction(figures, info, this, x - 2, y + 1);

        return info;
    }

    @Override
    public Figure copy() {
        return new Horse(team,x,y);
    }


}
