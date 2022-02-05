package valmx.nelly.chess;

import java.util.LinkedList;

public class Pawn extends Figure {
    public Pawn(int team, int x, int y) {
        super(team, x, y);
    }

    @Override
    public LinkedList<MoveInfo> getPossibleMoves(Figure[][] field) {
        LinkedList<MoveInfo> info = new LinkedList<>();

        int testY;
        if (team == 0) {
            testY = y - 1;
        } else {
            testY = y + 1;
        }

        if (testY < 0 || testY > 7) return info;

        Figure f = field[x][testY];

        if (f == null) {
            info.add(new MoveInfo(x, testY, MoveInfo.Action.MOVE));
            // Handling double move
            if (lastMove == -1) {
                int testY2;
                if (team == 0) {
                    testY2 = testY - 1;
                } else {
                    testY2 = testY + 1;
                }

                Figure f2 = field[x][testY2];

                if (f2 == null) {
                    info.add(new MoveInfo(x, testY2, MoveInfo.Action.MOVE));
                }

            }

        }

        // Handling Capturing

        if(x != 0) {
            Figure possibleCaptureLeft = field[x-1][testY];
            if(possibleCaptureLeft != null && possibleCaptureLeft.team != team) {
                info.add(new MoveInfo(x-1,testY, MoveInfo.Action.CAPTURE));

            }
        }

        if(x != 7) {
            Figure possibleCaptureRight = field[x+1][testY];
            if(possibleCaptureRight != null && possibleCaptureRight.team != team) {
                info.add(new MoveInfo(x+1,testY, MoveInfo.Action.CAPTURE));
            }
        }


        return info;
    }
}
