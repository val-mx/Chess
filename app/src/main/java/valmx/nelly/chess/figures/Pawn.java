package valmx.nelly.chess.figures;

import static valmx.nelly.chess.ChessView.ROUND;

import java.util.LinkedList;

public class Pawn extends Figure {

    public Pawn(boolean team, int x, int y) {
        super(team, x, y);
    }

    public int lastDoubleMove = -2;

    @Override
    public LinkedList<MoveInfo> getPossibleMoves(Figure[][] field) {
        LinkedList<MoveInfo> info = new LinkedList<>();

        int testY;
        if (!team) {
            testY = y - 1;
        } else {
            testY = y + 1;
        }

        if (testY < 0 || testY > 7) return info;

        Figure f = field[x][testY];

        if (f == null) {
            info.add(new MoveInfo(x, testY, MoveInfo.Action.PAWNMOVE, this));
            // Handling double move
            if (lastMove == -1) {
                int testY2;
                if (!team) {
                    testY2 = testY - 1;
                } else {
                    testY2 = testY + 1;
                }
                if(testY != 0 && testY != 7) {
                    Figure f2 = field[x][testY2];

                    if (f2 == null) {
                        info.add(new MoveInfo(x, testY2, MoveInfo.Action.PAWNMOVE_DOUBLE, this));
                    }
                }

            }

        }

        // Handling Capturing
        if(testY != 0 && testY != 7) {

            if (x != 0) {
                Figure possibleCaptureLeft = field[x - 1][testY];

                if (possibleCaptureLeft != null && possibleCaptureLeft.team != team) {
                    info.add(new MoveInfo(x - 1, testY, MoveInfo.Action.CAPTURE, this));
                } /*else
                    info.add(new MoveInfo(x - 1, testY, MoveInfo.Action.POSSIBLEPAWNCAPTURE, this));*/

            }
            if (testY != 0 && testY != 7) {

                if (x != 7) {
                    Figure possibleCaptureRight = field[x + 1][testY];

                    if (possibleCaptureRight != null && possibleCaptureRight.team != team) {
                        info.add(new MoveInfo(x + 1, testY, MoveInfo.Action.CAPTURE, this));
                    } /*else
                        info.add(new MoveInfo(x + 1, testY, MoveInfo.Action.POSSIBLEPAWNCAPTURE, this));*/

                }


                if (x != 0) {
                    Figure possibleEnPassantLeft = field[x - 1][y];

                    if (possibleEnPassantLeft != null && possibleEnPassantLeft.getPlayer() != team) {
                        if (possibleEnPassantLeft instanceof Pawn) {
                            if (((Pawn) possibleEnPassantLeft).lastDoubleMove == ROUND - 1) {
                                int captureY;
                                if (!team) {
                                    captureY = y - 1;
                                } else {
                                    captureY = y + 1;
                                }
                                info.add(new MoveInfo(x - 1, captureY, MoveInfo.Action.ENPASSANT, this));
                            }
                        }
                    }

                }

                if (x != 7) {
                    Figure possibleEnPassantLeft = field[x + 1][y];

                    if (possibleEnPassantLeft != null && possibleEnPassantLeft.getPlayer() != team) {
                        if (possibleEnPassantLeft instanceof Pawn) {
                            if (((Pawn) possibleEnPassantLeft).lastDoubleMove == ROUND - 1) {
                                int captureY;
                                if (!team) {
                                    captureY = y - 1;
                                } else {
                                    captureY = y + 1;
                                }
                                info.add(new MoveInfo(x + 1, captureY, MoveInfo.Action.ENPASSANT, this));
                            }
                        }
                    }

                }

            }
        }
        return info;
    }

    @Override
    public Figure copy() {
        final Pawn pawn = new Pawn(team, x, y);

        pawn.lastMove = lastMove;

        return pawn;
    }
}
