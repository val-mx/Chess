package valmx.nelly.chess;

import java.util.LinkedList;
import java.util.Stack;

import valmx.nelly.chess.figures.Bishop;
import valmx.nelly.chess.figures.Figure;
import valmx.nelly.chess.figures.Horse;
import valmx.nelly.chess.figures.King;
import valmx.nelly.chess.figures.MoveInfo;
import valmx.nelly.chess.figures.Pawn;
import valmx.nelly.chess.figures.Queen;
import valmx.nelly.chess.figures.Rook;

public class ChessBoard {
    class LastActionInfo {
        private final Figure replacedFig;
        private final MoveInfo i;

        public LastActionInfo(Figure replacedFig, MoveInfo i) {

            this.replacedFig = replacedFig;
            this.i = i;
        }

        public Figure getReplacedFig() {
            return replacedFig;
        }

        public MoveInfo getI() {
            return i;
        }
    }


    private Stack<LastActionInfo> lastActionStack = new Stack<>();

    private Figure[][] board = new Figure[8][8];

    private boolean player;

    private int round = 0;

    public boolean getPlayer() {
        return player;
    }

    public void setPlayer(boolean player) {
        this.player = player;
    }

    public void changePlayer() {
        player = !player;
    }

    public ChessBoard() {
        initGame();
    }

    public ChessBoard(Figure[][] board) {
        this.board = board;
    }

    private void initGame() {
        for (int i = 0; i < 8; i++) {
            board[i][1] = new Pawn(true, i, 1);
        }
        for (int i = 0; i < 8; i++) {
            board[i][6] = new Pawn(false, i, 6);
        }
        board[0][0] = new Rook(true, 0, 0);
        board[2][0] = new Bishop(true, 2, 0);
        board[5][0] = new Bishop(true, 5, 0);
        board[7][0] = new Rook(true, 7, 0);
        board[4][0] = new Queen(true, 4, 0);
        board[3][0] = new King(true, 3, 0);
        board[1][0] = new Horse(true, 1, 0);
        board[6][0] = new Horse(true, 6, 0);

        board[0][7] = new Rook(false, 0, 07);
        board[2][7] = new Bishop(false, 2, 07);
        board[5][7] = new Bishop(false, 5, 07);
        board[7][7] = new Rook(false, 7, 7);
        board[4][7] = new Queen(false, 4, 07);
        board[3][7] = new King(false, 3, 7);
        board[1][7] = new Horse(false, 1, 07);
        board[6][7] = new Horse(false, 6, 07);
    }


    public Figure[][] getBoard() {
        return board;
    }

    public boolean isKingInCheck(Figure[][] board) {
        final LinkedList<MoveInfo> allPossibleMoves = getAllPossibleMoves();

        for (MoveInfo m : allPossibleMoves) {

            final Figure figure = getFigure(m.getX(), m.getY());

            if (figure != null) {
                if (figure instanceof King /*&& getFigure(m.getX(),m.getY()).getTeam() == player*/) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isKingInCheck() {
        return isKingInCheck(board);
    }

    public LinkedList<MoveInfo> getLegalCheckMoves() {
        LinkedList<MoveInfo> result = new LinkedList<>();

        ChessBoard testBoard = new ChessBoard(copyBoard(board));
        final LinkedList<MoveInfo> allPossibleMovesPlayer = getAllPossibleMoves();


        testBoard.setPlayer(player);

        for (MoveInfo i : allPossibleMovesPlayer) {
            testBoard.doAction(i);
            if (!testBoard.isKingInCheck()) result.add(i);
            testBoard.undoLastAction();
        }

        return result;
    }

    private LinkedList<MoveInfo> getAllPossibleMoves() {
        final LinkedList<MoveInfo> allPossibleMovesPlayer = new LinkedList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                Figure f = board[i][j];
                if(f!= null && f.getPlayer() ==player)
                    allPossibleMovesPlayer.addAll(f.getPossibleMoves(board));
            }
        }
        return allPossibleMovesPlayer;
    }

    public void doAction(MoveInfo i) {
        doAction(i, board);
    }
    public void incrementRound() {
        round++;
    }

    public void doAction(MoveInfo i, Figure[][] board) {

//        i.getActor().setLastMove(round);

        switch (i.getAction()) {
            case ENPASSANT:
                final Figure actor1 = i.getActor();

                int testY = i.getY();
                if (actor1.getPlayer()) {
                    testY++;
                } else testY--;

                setFigure(i.getX(), i.getY(), actor1);
                setFigure(actor1.getX(), actor1.getY(), null);
                final Figure captor1 = getFigure(i.getX(), testY);
                lastActionStack.add( new LastActionInfo(captor1, i));
                setFigure(i.getX(), testY, null);

                break;
            case ROCHADE_LEFT: case ROCHADE_RIGHT:
                final Figure actor2 = i.getActor();


                if(i.getAction() == MoveInfo.Action.ROCHADE_LEFT) {

                    Figure rook = getFigure(0,actor2.getY());

                    setFigure(rook.getX(),rook.getY(),null);
                    setFigure(2,rook.getY(),rook);

                    setFigure(actor2.getX(),actor2.getY(),null);
                    setFigure(1,actor2.getY(),actor2);
                } else {
                    Figure rook = getFigure(7,actor2.getY());

                    setFigure(rook.getX(),rook.getY(),null);
                    setFigure(4,rook.getY(),rook);

                    setFigure(actor2.getX(),actor2.getY(),null);
                    setFigure(5,actor2.getY(),actor2);
                }

            case MOVE:
            case CAPTURE:
            case PAWNMOVE_DOUBLE:
            case PAWNMOVE:
                final Figure actor = i.getActor();
                final Figure captor = getFigure(i.getX(), i.getY());
                lastActionStack.add(new LastActionInfo(captor, i));

                setFigure(actor.getX(), actor.getY(), null);
                setFigure(i.getX(), i.getY(), actor);
                break;


        }

        round++;


    }

    public void undoLastAction() {
        if (!lastActionStack.empty()) {
            LastActionInfo lastAction = lastActionStack.pop();


            final int y1 = lastAction.getI().getY();
            final int x1 = lastAction.getI().getX();
            board[x1][y1] = null;
            board[x1][y1] = lastAction.replacedFig;
            int x = lastAction.i.getFromX();
            int y = lastAction.i.getFromY();
            final Figure actor = lastAction.i.getActor();
            board[x][y] = lastAction.i.getActor();
            actor.setX(x);
            actor.setY(y);
//            actor.setLastMove(--round);

        }
    }

    public LinkedList<MoveInfo> getLegalMoves(Figure f) {
        if (isKingInCheck()) {
            final LinkedList<MoveInfo> legalCheckMoves = getLegalCheckMoves();
            LinkedList<MoveInfo> possibleMoves = f.getPossibleMoves(board);
            LinkedList<MoveInfo> result = new LinkedList<>();
            for (MoveInfo m : legalCheckMoves) {

                for (MoveInfo m1 : possibleMoves) {

                    if (m.getX() == m1.getX() && m.getY() == m1.getY())
                        if (m1.getActor().getX() == f.getX() && m1.getActor().getY() == f.getY())
                            if (m.getActor().getClass().equals(m1.getActor().getClass()))
                                result.add(m);
                }

            }

            return result;

        } else {

            final LinkedList<MoveInfo> possibleMoves = f.getPossibleMoves(board);

            LinkedList<MoveInfo> result = new LinkedList<>();

            ChessBoard testBoard = new ChessBoard(board);

            testBoard.setPlayer(player);

            possibleMoves.forEach(m -> {

                testBoard.doAction(m);
                if (!testBoard.isKingInCheck()) result.add(m);
                testBoard.undoLastAction();

            });

            return result;
        }

    }

    public Figure[][] copyBoard(Figure[][] arrayToCopy) {
        Figure[][] result = new Figure[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                final Figure figure = arrayToCopy[x][y];
                if (figure != null)
                    result[x][y] = figure.copy();
            }
        }
        return result;
    }


    public Figure getFigure(int x, int y) {
        return board[x][y];
    }

    public void setFigure(int x, int y, Figure fig) {
        board[x][y] = fig;
        if (fig != null) {
            fig.setX(x);
            fig.setY(y);
        }
    }
}
