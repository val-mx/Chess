package valmx.nelly.chess.figures;

import java.util.LinkedList;

public class Queen extends Figure {
    public Queen(int team, int x, int y) {
        super(team, x, y);
    }

    @Override
    public LinkedList<MoveInfo> getPossibleMoves(Figure[][] field) {

        LinkedList<MoveInfo> info = new LinkedList<>();

        info.addAll(new Rook(team,x,y).getPossibleMoves(field));
        info.addAll(new Bishop(team,x,y).getPossibleMoves(field));

        return info;
    }
}