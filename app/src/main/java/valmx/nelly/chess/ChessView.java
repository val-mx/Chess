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

public class ChessView extends androidx.appcompat.widget.AppCompatImageView implements View.OnTouchListener {
    private Canvas c = new Canvas();
    private Paint check0, check1;
    private Paint withePaint, blackPaint;
    private Float dx;
    private Figure[][] board = new Figure[8][8];

    public ChessView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        check0 = new Paint();
        check1 = new Paint();
        blackPaint = new Paint();
        withePaint = new Paint();

        check0.setColor(getResources().getColor(R.color.colorPrimary));
        check1.setColor(getResources().getColor(R.color.colorPrimaryDark));
        withePaint.setColor(getResources().getColor(R.color.white));
        blackPaint.setColor(getResources().getColor(R.color.black));

        post(() -> {
            Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            setBackground(new BitmapDrawable(bm));
            c.setBitmap(bm);

            drawCheckerBoard();
            drawFigures();

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

                c.drawText("R",dx*(x+.5F), dx * y + dx * .5F,withePaint);

            }
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case ACTION_DOWN:


        }

        return false;
    }
}
