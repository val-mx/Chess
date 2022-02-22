package valmx.nelly.chess;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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
    public static int ROUND = 1;
    private final Canvas c = new Canvas();
    private final Paint check0;
    private final Paint check1;
    private final Paint withePaint;
    private final Paint blackPaint;
    private final Paint helpPaint;
    private final Paint toInfoPaint;
    private final Paint fromInfoPaint;
    private final Paint textPaint;
    private final DrawableManager drawableManager;
    int counter = 0;
    boolean isKingThreatened;
    boolean isAnimationActive = false;
    int activeAnimations = 0;
    LinkedList<AnimationBundle> animationsBundles = new LinkedList<>();
    private Float dx;
    private boolean playerToMove = false;
    private ChessBoard chessBoard;
    private ChessListener listener = null;
    private Figure activeFigure = null;
    private LinkedList<MoveInfo> activeMoveInfo = null;
    private MoveInfo lastMove;

    public ChessView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        check0 = new Paint();
        check1 = new Paint();
        blackPaint = new Paint();
        withePaint = new Paint();
        helpPaint = new Paint();
        toInfoPaint = new Paint();
        fromInfoPaint = new Paint();
        textPaint = new Paint();

        drawableManager = new DrawableManager(getResources());

        helpPaint.setColor(getResources().getColor(R.color.help));
        check0.setColor(getResources().getColor(R.color.colorPrimary));
        check1.setColor(getResources().getColor(R.color.colorPrimaryDark));
        withePaint.setColor(getResources().getColor(R.color.white));
        blackPaint.setColor(getResources().getColor(R.color.black));
        fromInfoPaint.setColor(getResources().getColor(R.color.colorFrom));
        toInfoPaint.setColor(getResources().getColor(R.color.colorTo));
        textPaint.setColor(getResources().getColor(R.color.white));

        setOnTouchListener(this::onTouch);

        post(() -> {

            getLayoutParams().height = getWidth();

            Bitmap bm = Bitmap.createBitmap(getWidth(), getWidth(), Bitmap.Config.ARGB_8888);
            setBackground(new BitmapDrawable(bm));
            c.setBitmap(bm);


            chessBoard = new ChessBoard();


            drawCheckerBoard();
            drawFigures();
            doBotAction();
            invalidate();

        });


    }

    public void setListener(ChessListener listener) {
        this.listener = listener;
    }

    private void drawCheckerBoard() {

        dx = (getWidth() / 8F);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                RectF rect = new RectF(x * dx, y * dx, (x + 1) * dx, (y + 1) * dx);

                if (lastMove != null) {
                    if (y == lastMove.getY()) {
                        if (x == lastMove.getX()) {
                            c.drawRect(rect, toInfoPaint);
                            continue;
                        }
                    }

                    if (y == lastMove.getFromY()) {
                        if (x == lastMove.getFromX()) {
                            c.drawRect(rect, fromInfoPaint);
                            continue;
                        }
                    }
                }
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
        Figure[][] board = chessBoard.getBoard();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                Figure f = board[x][y];

                if (f == null) continue;

                if (!f.drawMe) continue;

                Drawable drawable = getDrawableForActor(f);


                if (drawable != null) {
                    drawable.setBounds(Integer.parseInt(String.valueOf(dx * x).split("\\.")[0]), Integer.parseInt(String.valueOf(dx * y).split("\\.")[0]), Integer.parseInt(String.valueOf((x + 1) * dx).split("\\.")[0]), Integer.parseInt(String.valueOf((y + 1) * dx).split("\\.")[0]));
                    drawable.draw(c);
                }
            }
        }
    }

    public Drawable getDrawableForActor(Figure f) {
        Drawable drawable = null;
        if (!f.getPlayer()) {
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
        return drawable;
    }

    private boolean doBotAction() {


        ResultRunnable r = i -> {
            doAction(i.getActor(), i);
            drawRoutine();


        };
        WeightCalculator.getBestPossibleMove(chessBoard.getBoard(), r);


        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case ACTION_DOWN:
                counter = 0;

                int x = (int) (event.getX() / dx);
                int y = (int) (event.getY() / dx);

                if (y > 7) return false;

                Figure temp = chessBoard.getFigure(x, y);

                if (activeMoveInfo != null) {

                    for (MoveInfo i : activeMoveInfo) {
                        if (i.getAction() == MoveInfo.Action.POSSIBLEPAWNCAPTURE) continue;

                        if (i.getY() == y && i.getX() == x) {
                            doAction(activeFigure, i);
                            activeFigure = null;
                            activeMoveInfo = null;
                            doBotAction();
                            if (!isAnimationActive)

                                drawRoutine();
                            return true;
                        }

                    }
                }

                if (temp == null) {

                    activeMoveInfo = null;
                    activeFigure = null;

                } else {
                    if (!chessBoard.getPlayer() == temp.getPlayer()) {
                        activeFigure = temp;

                        activeMoveInfo = chessBoard.getLegalMoves(temp);
                    }
                }

                if (!isAnimationActive)
                    drawRoutine();
                break;
            case ACTION_MOVE:
                counter++;
                break;
            case ACTION_UP:
                if (counter > 8) {
                    event.setAction(ACTION_DOWN);
                    onTouch(v, event);
                    return false;
                }
        }

        return true;
    }

    private void doAction(Figure f, MoveInfo i) {

        Figure[][] board = chessBoard.getBoard();

        if (i.getAction() == MoveInfo.Action.ROCHADE_RIGHT || i.getAction() == MoveInfo.Action.ROCHADE_LEFT) {
            int rookX = 7;
            if (i.getAction() == MoveInfo.Action.ROCHADE_LEFT) {
                rookX = 0;
            }

            Rook rook = (Rook) board[rookX][f.getY()];
//            animateMove(new MoveInfo(rookX, f.getY(), MoveInfo.Action.MOVE, rook));
            animateMove(i);
        }
        animateMove(i);
        chessBoard.doAction(i);

        f.setLastMove(ROUND);

        chessBoard.changePlayer();
        listener.onNewRound(chessBoard);

    }

    private void drawRoutine() {
        drawCheckerBoard();
        drawCoordinateHelpers();
        drawFigures();
        drawPossibleMoves(activeMoveInfo);
        invalidate();
    }

    public void animateMove(MoveInfo i) {

        final Figure actor = i.getActor();
        final ValueAnimator animator = ValueAnimator.ofFloat(0F, 1F);

        float startX = actor.getX() * dx;
        float endX = i.getX() * dx;
        float startY = actor.getY() * dx;
        float endY = i.getY() * dx;

        Float diffX = startX - endX;
        Float diffY = startY - endY;


        animator.setDuration(200L);
        isAnimationActive = true;
        actor.drawMe = false;
        activeAnimations++;

        animator.addUpdateListener(l -> {
            Float value = (Float) l.getAnimatedValue();
            animationsBundles.add(new AnimationBundle(startX - diffX * value, startY - diffY * value, actor));
            tryToExecuteAnims();
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                actor.drawMe = true;
                if (!isAnimationActive)

                    drawRoutine();
                isAnimationActive = false;
                activeAnimations--;
                tryToExecuteAnims();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();


    }

    public void tryToExecuteAnims() {

        if (animationsBundles.size() >= activeAnimations) {
            drawRoutine();
            animationsBundles.forEach(b -> {
                drawFigAtLocation(b.getF(), b.getX(), b.getY());
            });
            invalidate();
            animationsBundles = new LinkedList<>();
        }
    }

    public void drawFigAtLocation(Figure actor, float x, float y) {
        Drawable drawable = getDrawableForActor(actor);
        drawable.setBounds(new Rect((int) x, (int) y, (int) (x + dx), (int) (y + dx)));
        drawable.draw(c);
    }

    public void drawPossibleMoves(LinkedList<MoveInfo> info) {
        if (info == null) return;
        info.forEach(i -> {
                    if (i.getAction() != MoveInfo.Action.POSSIBLEPAWNCAPTURE) {
                        int x = i.getX();
                        int y = i.getY();
                        c.drawCircle((x + .5F) * dx, (y + .5F) * dx, dx * .2F, helpPaint);
                    }
                }
        );
    }

    private void drawCoordinateHelpers() {
        for (int i = 8; i > 0; i--) {

            float y = (dx * (i - 1)) - dx / 12F;
            textPaint.setTextSize(dx / 6F);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float height = fm.descent - fm.ascent;
            c.drawText("" + (i), 0, y + height, textPaint);

        }

        for (int i = 8; i > 0; i--) {

            float x = (dx * (i)) - dx / 12F;
            textPaint.setTextSize(dx / 6F);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float height = fm.descent - fm.ascent;
            c.drawText("" + (char) ('a'-1 +i), x - height, 8*dx, textPaint);

        }


    }

    interface ChessListener {

        public void onNewRound(ChessBoard board);
    }

    public interface ResultRunnable {
        void run(MoveInfo i);
    }

    protected class AnimationBundle {
        private final float x;
        private final float y;
        private final Figure f;

        public AnimationBundle(float x, float y, Figure f) {
            this.x = x;
            this.y = y;
            this.f = f;
        }

        public Figure getF() {
            return f;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }
}
