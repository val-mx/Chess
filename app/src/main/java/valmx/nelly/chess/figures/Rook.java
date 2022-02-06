package valmx.nelly.chess.figures;

import java.util.LinkedList;

public class Rook extends Figure {
    public Rook(int team, int x, int y) {
        super(team, x, y);
    }


    public LinkedList<MoveInfo> getPossibleMoves(Figure[][] field) {

        LinkedList<MoveInfo> possibleMoves = new LinkedList<>();

        // Y UP

        for (int i = 1; i < 8; i++) {
            int testY = y - i;

            if (testY == -1) break;

            Figure figure = field[x][testY];

            if (figure == null) {
                possibleMoves.add(new MoveInfo(x, testY, MoveInfo.Action.MOVE,this));
            } else if (figure.team == team) {
                break;
            } else {
                possibleMoves.add(new MoveInfo(x, testY, MoveInfo.Action.CAPTURE,this));
                break;
            }
        }
        // Y DOWN

        for (int i = 1; true; i++) {
            int testY = y + i;

            if (testY == 8) break;

            Figure figure = field[x][testY];
            if (figure == null) {
                possibleMoves.add(new MoveInfo(x, testY, MoveInfo.Action.MOVE,this));
            } else if (figure.team == team) {
                break;
            } else {
                possibleMoves.add(new MoveInfo(x, testY, MoveInfo.Action.CAPTURE,this));
                break;
            }
        }

        // X UP

        for (int i = 1; i < 8; i++) {
            int testX = x- i;

            if (testX == -1) break;

            Figure figure = field[testX][y];

            if (figure == null) {
                possibleMoves.add(new MoveInfo(testX, y, MoveInfo.Action.MOVE,this));
            } else if (figure.team == team) {
                break;
            } else {
                possibleMoves.add(new MoveInfo(testX, y, MoveInfo.Action.CAPTURE,this));
                break;
            }
        }
        // X DOWN

        for (int i = 1; true; i++) {
            int testX = x + i;

            if (testX == 8) break;

            Figure figure = field[testX][y];
            if (figure == null) {
                possibleMoves.add(new MoveInfo(testX, y, MoveInfo.Action.MOVE,this));
            } else if (figure.team == team) {
                break;
            } else {
                possibleMoves.add(new MoveInfo(testX, y, MoveInfo.Action.CAPTURE,this));
                break;
            }
        }

        return possibleMoves;
    }
}
