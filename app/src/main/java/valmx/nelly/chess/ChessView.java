package valmx.nelly.chess;

import static android.view.MotionEvent.ACTION_DOWN;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;

import valmx.nelly.chess.figures.Bishop;
import valmx.nelly.chess.figures.Figure;
import valmx.nelly.chess.figures.Horse;
import valmx.nelly.chess.figures.King;
import valmx.nelly.chess.figures.MoveInfo;
import valmx.nelly.chess.figures.Pawn;
import valmx.nelly.chess.figures.Queen;
import valmx.nelly.chess.figures.Rook;
import valmx.nelly.chess.managers.DrawableManager;

public class ChessView extends androidx.appcompat.widget.AppCompatImageView implements View.OnTouchListener {
    private Canvas c = new Canvas();
    private Paint check0, check1;
    private Paint withePaint, blackPaint, helpPaint;
    private Float dx;
    private Figure[][] board = new Figure[8][8];
    private int roundCounter = 1;
    private int playerToMove = 1;

    private DrawableManager drawableManager;

    private Figure activeFigure = null;
    private LinkedList<MoveInfo> activeMoveInfo = null;

    public ChessView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        check0 = new Paint();
        check1 = new Paint();
        blackPaint = new Paint();
        withePaint = new Paint();
        helpPaint = new Paint();

        drawableManager = new DrawableManager(getResources());

        helpPaint.setColor(getResources().getColor(R.color.help));
        check0.setColor(getResources().getColor(R.color.colorPrimary));
        check1.setColor(getResources().getColor(R.color.colorPrimaryDark));
        withePaint.setColor(getResources().getColor(R.color.white));
        blackPaint.setColor(getResources().getColor(R.color.black));
        setOnTouchListener(this::onTouch);

        post(() -> {
            Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            setBackground(new BitmapDrawable(bm));
            c.setBitmap(bm);

            for (int i = 0; i < 8; i++) {
                board[i][1] = new Pawn(1, i, 1);
            }
            for (int i = 0; i < 8; i++) {
                board[i][6] = new Pawn(0, i, 6);
            }

            board[0][0] = new Rook(1, 0, 0);
            board[2][0] = new Bishop(1, 2, 0);
            board[5][0] = new Bishop(1, 5, 0);
            board[7][0] = new Rook(1, 7, 0);
            board[3][0] = new Queen(1, 3, 0);
            board[4][0] = new King(1, 4, 0);
            board[1][0] = new Horse(1, 1, 0);
            board[6][0] = new Horse(1, 6, 0);


            board[0][7] = new Rook(0, 0, 07);
            board[2][7] = new Bishop(0, 2, 07);
            board[5][7] = new Bishop(0, 5, 07);
            board[7][7] = new Rook(0, 7, 7);
            board[4][7] = new Queen(0, 4, 07);
            board[3][7] = new King(0, 3, 07);
            board[1][7] = new Horse(0, 1, 07);
            board[6][7] = new Horse(0, 6, 07);

            drawCheckerBoard();
            drawFigures();
            doBotAction();
            invalidate();

        });


    }

    private void drawCheckerBoard() {

        dx = (getWidth() / 8F);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                RectF rect = new RectF(x * dx, y * dx, (x + 1) * dx, (y + 1) * dx);

                if (y % 2 == 0) {
                    if (x % 2 == 1) {
                        c.drawRect(rect, check1);
                    } else c.drawRect(rect, check0);
                } else {
                    if (x % 2 == 0) {
                        c.drawRect(rect, check1);
                    } else c.drawRect(rect, check0);

                }

            }
        }
        invalidate();

    }

    private void drawFigures() {
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                Figure f = board[x][y];

                if (f == null) continue;

                Paint p;

                Drawable drawable = null;
                if (f.getTeam() == 0) {
                    if (f instanceof Rook) {
                        drawable = drawableManager.BLACK_ROOK;
                    } else if (f instanceof Pawn) {
                        drawable = drawableManager.BLACK_PAWN;
                    } else if (f instanceof Bishop) {
                        drawable = drawableManager.BLACK_BISHOP;

                    } else if (f instanceof Queen) {
                        drawable = drawableManager.BLACK_QUEEN;
                    } else if (f instanceof King) {
                        drawable = drawableManager.BLACK_KING;
                    } else if (f instanceof Horse) {
                        drawable = drawableManager.BLACK_KNIGHT;
                    }

                } else if (f instanceof Rook) {
                    drawable = drawableManager.WHITE_ROOK;
                } else if (f instanceof Pawn) {
                    drawable = drawableManager.WHITE_PAWN;
                } else if (f instanceof Bishop) {
                    drawable = drawableManager.WHITE_BISHOP;

                } else if (f instanceof Queen) {
                    drawable = drawableManager.WHITE_QUEEN;
                } else if (f instanceof King) {
                    drawable = drawableManager.WHITE_KING;
                } else if (f instanceof Horse) {
                    drawable = drawableManager.WHITE_KNIGHT;
                }

                if (drawable != null) {
                    drawable.setBounds(Integer.parseInt(String.valueOf(dx * x).split("\\.")[0]), Integer.parseInt(String.valueOf(dx * y).split("\\.")[0]), Integer.parseInt(String.valueOf((x + 1) * dx).split("\\.")[0]), Integer.parseInt(String.valueOf((y + 1) * dx).split("\\.")[0]));
                    drawable.draw(c);
                    continue;
                }
                if (f.getTeam() == 1) p = withePaint;
                else p = blackPaint;

                p.setTextSize(dx / 2);

                if (f instanceof Rook)
                    c.drawText("R", dx * (x + .5F), dx * y + dx * .5F, p);
                else if (f instanceof Pawn) {
                    c.drawText("P", dx * (x + .5F), dx * y + dx * .5F, p);
                } else if (f instanceof Bishop) {
                    c.drawText("B", dx * (x + .5F), dx * y + dx * .5F, p);
                } else if (f instanceof Queen) {
                    c.drawText("Q", dx * (x + .5F), dx * y + dx * .5F, p);
                } else if (f instanceof King) {
                    c.drawText("K", dx * (x + .5F), dx * y + dx * .5F, p);
                }
                if (f instanceof Horse) {
                    c.drawText("H", dx * (x + .5F), dx * y + dx * .5F, p);
                }
            }
        }
    }

    private boolean doBotAction() {
        return doBotAction(WeightCalculator.getWeights(board, 1));

    }

    private boolean doBotAction(int[][] weights) {


        int maxX = 0;
        int maxY = 0;
        int maxW = -100;

        for (int x = 0; x < weights.length; x++) {
            for (int y = 0; y < weights.length; y++) {
                final int weight = weights[x][y];

                if (maxW < weight) {
                    maxW = weight;
                    maxX = x;
                    maxY = y;
                }
            }
        }


        LinkedList<MoveInfo> allPossibleMoves = WeightCalculator.getAllPossibleMoves(board, 1);

        boolean actionDone = false;

        for (MoveInfo move : allPossibleMoves) {
            if (move.getY() == maxY)
                if (move.getX() == maxX) {
                    if (move.getAction() != MoveInfo.Action.POSSIBLEPAWNCAPTURE) {
                        doAction(move.getActor(), move);
                        actionDone = true;
                        break;
                    }
                }
        }
        if (!actionDone) {
            weights[maxX][maxY] = -1000;
            doBotAction(weights);
        }


        return true;
    }

    private void drawWeights() {

        int team = roundCounter % 2;

        int[][] weights = WeightCalculator.getWeights(board, team);

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {

                int weight = weights[x][y];

                if (weight == 0) continue;
                Paint p;
                if (roundCounter % 2 == 0) p = withePaint;
                else p = blackPaint;

                p.setTextSize(dx / 4);

                c.drawText(weight + "", dx * x, dx * y + dx * .5F, p);
            }
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case ACTION_DOWN:

                int x = (int) (event.getX() / dx);
                int y = (int) (event.getY() / dx);

                if (y > 7) return false;

                Figure temp = board[x][y];

                if (activeMoveInfo != null) {

                    for (MoveInfo i : activeMoveInfo) {
                        if (i.getAction() == MoveInfo.Action.POSSIBLEPAWNCAPTURE) continue;

                        if (i.getY() == y && i.getX() == x) {
                            doAction(activeFigure, i);
                            activeFigure = null;
                            activeMoveInfo = null;
                            doBotAction();
                            drawRoutine();
                            return true;
                        }

                    }
                }

                if (temp == null) {

                    activeMoveInfo = null;
                    activeFigure = null;

                } else {

                    if (temp.getTeam() == playerToMove) {
                        activeFigure = temp;
                        activeMoveInfo = temp.getPossibleMoves(board);
                    }
                }

                drawRoutine();


        }

        return false;
    }

    private void doAction(Figure f, MoveInfo i) {
        board[f.getX()][f.getY()] = null;
        f.setX(i.getX());
        f.setY(i.getY());
        f.setLastMove(roundCounter);
        board[i.getX()][i.getY()] = f;
        roundCounter++;
        playerToMove = roundCounter % 2;
    }

    private void drawRoutine() {
        drawCheckerBoard();
        drawFigures();
        drawPossibleMoves(activeMoveInfo);
        drawWeights();
        invalidate();
    }

    public void drawPossibleMoves(LinkedList<MoveInfo> info) {
        if (info == null) return;
        info.forEach(i -> {
                    if (i.getAction() != MoveInfo.Action.POSSIBLEPAWNCAPTURE) {
                        int x = i.getX();
                        int y = i.getY();
                        c.drawCircle((x + .5F) * dx, (y + .5F) * dx, dx * .15F, helpPaint);
                    }
                }
        );
    }
}
