package valmx.nelly.chess;

import static android.view.MotionEvent.ACTION_DOWN;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;

public class ChessView extends androidx.appcompat.widget.AppCompatImageView implements View.OnTouchListener {
    private Canvas c = new Canvas();
    private Paint check0, check1;
    private Paint withePaint, blackPaint, helpPaint;
    private Float dx;
    private Figure[][] board = new Figure[8][8];
    private int roundCounter = 1;
    private int playerToMove = 1;

    private Figure activeFigure = null;
    private LinkedList<MoveInfo> activeMoveInfo = null;

    public ChessView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        check0 = new Paint();
        check1 = new Paint();
        blackPaint = new Paint();
        withePaint = new Paint();
        helpPaint = new Paint();

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

            drawCheckerBoard();
            drawFigures();
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

                if (f.team == 1) p = withePaint;
                else p = blackPaint;

                p.setTextSize(dx / 2);

                if (f instanceof Rook)
                    c.drawText("R", dx * (x + .5F), dx * y + dx * .5F, p);
                else if (f instanceof Pawn) {
                    c.drawText("P", dx * (x + .5F), dx * y + dx * .5F, p);
                } else if (f instanceof Bishop) {
                    c.drawText("B", dx * (x + .5F), dx * y + dx * .5F, p);
                }else if (f instanceof Queen) {
                    c.drawText("Q", dx * (x + .5F), dx * y + dx * .5F, p);
                }
            }
        }
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case ACTION_DOWN:

                int x = (int) (event.getX() / dx);
                int y = (int) (event.getY() / dx);

                Figure temp = board[x][y];

                if (activeMoveInfo != null) {

                    for (MoveInfo i : activeMoveInfo) {

                        if (i.getY() == y && i.getX() == x) {
                            doAction(activeFigure, i);
                            activeFigure = null;
                            activeMoveInfo = null;
                            drawRoutine();
                            return true;
                        }

                    }
                }

                if (temp == null) {

                    activeMoveInfo = null;
                    activeFigure = null;

                } else {

                    if (temp.team == playerToMove) {
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
        invalidate();
    }

    public void drawPossibleMoves(LinkedList<MoveInfo> info) {
        if (info == null) return;
        info.forEach(i -> {
                    int x = i.getX();
                    int y = i.getY();
                    c.drawCircle((x + .5F) * dx, (y + .5F) * dx, dx * .15F, helpPaint);
                }
        );
    }
}
