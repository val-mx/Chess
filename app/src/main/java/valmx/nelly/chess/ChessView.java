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
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

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
    public static int ROUND = 1;
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
            board[4][0] = new Queen(1, 4, 0);
            board[3][0] = new King(1, 3, 0);
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

            Log.i("WERT DER STELLUNG", WeightCalculator.getWortSum(board) + "");

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

    boolean isKingThreatened;

    private void onTurnChange() {
        isKingThreatened = false;
//        playerToMove;

        AtomicReference<Figure> king = new AtomicReference<>();

        HashMap<Figure, MoveInfo> attackers = new HashMap<>();

        final LinkedList<MoveInfo> allPossibleMoves = WeightCalculator.getAllPossibleMoves(board, playerToMove);

        allPossibleMoves.forEach(m -> {
            if (m.getAction() == MoveInfo.Action.CAPTURE) {

                Figure attackedFig = board[m.getX()][m.getY()];

                if (attackedFig instanceof King && attackedFig.getTeam()
                        == playerToMove) {
                    attackers.put(m.getActor(), m);
                    king.set(attackedFig);
                }

            }
        });

        LinkedList<MoveInfo> allowedMovesToSaveKing = new LinkedList<>(king.get().getPossibleMoves(board));

        Figure k = king.get();

        attackers.forEach((a,m) -> {
            final LinkedList<MoveInfo> possibleMoves = a.getPossibleMoves(board);
            allowedMovesToSaveKing.add(new MoveInfo(a.getX(),a.getY(),null,null));
            possibleMoves.forEach(i -> {
                if (i.getAction() != MoveInfo.Action.CAPTURE) {
//                    if(i.getX()>)
                }
            });
        });

    }

    private void drawFigures() {
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                Figure f = board[x][y];

                if (f == null) continue;

                if (!f.drawMe) continue;

                Paint p;

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
        return drawable;
    }

    public interface ResultRunnable {
        public void run(MoveInfo i);
    }


    private boolean doBotAction() {


        ResultRunnable r = i -> {
            doAction(i.getActor(),i);
            drawRoutine();
            Toast.makeText(getContext(),i.toString(),Toast.LENGTH_LONG).show();

        };
        WeightCalculator.getBestPossibleMove(board, r);


        return true;
    }

    private void drawWeights() {

        int team = ROUND % 2;

        int[][] weights = new int[9][8];

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {

                int weight = weights[x][y];

                if (weight == 0) continue;
                Paint p;
                if (ROUND % 2 == 0) p = withePaint;
                else p = blackPaint;

                p.setTextSize(dx / 4);

                c.drawText(weight + "", dx * x, dx * y + dx * .5F, p);
            }
        }
    }

    int counter = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case ACTION_DOWN:
                counter = 0;

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

                    if (temp.getTeam() == 0 && ROUND%2==0) {
                        activeFigure = temp;
                        activeMoveInfo = temp.getPossibleMoves(board);
                    }
                }

                if (!isAnimationActive)
                    drawRoutine();
                break;
            case ACTION_MOVE:
                counter++;
                break;
            case ACTION_UP:
                if(counter>8) {
                    event.setAction(ACTION_DOWN);
                    onTouch(v,event);
                    return false;
                }
        }

        return true;
    }

    private void doAction(Figure f, MoveInfo i) {


        /*if (i.getAction() == MoveInfo.Action.ROCHADE_LEFT) {
            board[f.getX()][f.getY()] = null;
            board[f.getX() - 2][f.getY()] = f;
            board[f.getX() - 1][f.getY()] = board[0][f.getY()];
            board[0][f.getY()] = null;


            f.setX(f.getX() - 2);
            board[f.getX() - 1][f.getY()].setX(f.getX() - 1);

        } else */
        if (i.getAction() == MoveInfo.Action.ROCHADE_RIGHT || i.getAction() == MoveInfo.Action.ROCHADE_LEFT) {

            int rookX = f.getX() + 1;
            int kingX = f.getX() + 2;
            int previousRookX = 7;

            if (i.getAction() == MoveInfo.Action.ROCHADE_LEFT) {
                rookX = f.getX() - 1;
                kingX = f.getX() - 2;
                previousRookX = 0;
            }

            board[f.getX()][f.getY()] = null;
            board[kingX][f.getY()] = f;
            board[rookX][f.getY()] = board[previousRookX][f.getY()];
            board[previousRookX][f.getY()] = null;
            Rook rook = (Rook) board[rookX][f.getY()];
            animateMove(new MoveInfo(rookX, f.getY(), MoveInfo.Action.MOVE, rook));
            animateMove(i);
            rook.setX(rookX);
            f.setX(kingX);

        } else {
            animateMove(i);
            board[f.getX()][f.getY()] = null;
            f.setX(i.getX());
            f.setY(i.getY());
            board[i.getX()][i.getY()] = f;

            if (i.getAction() == MoveInfo.Action.ENPASSANT) {

                int testY = i.getY();

                if (f.getTeam() == 0) {
                    testY++;
                } else testY--;

                board[i.getX()][testY] = null;

            }

        }

        if (i.getAction() == MoveInfo.Action.PAWNMOVE_DOUBLE) {
            ((Pawn) i.getActor()).lastDoubleMove = ROUND;
        }

        f.setLastMove(ROUND);
        ROUND++;
        playerToMove = ROUND % 2;
    }

    private void drawRoutine() {
        drawCheckerBoard();
        drawFigures();
        drawPossibleMoves(activeMoveInfo);
//        drawWeights();
        invalidate();
    }

    boolean isAnimationActive = false;
    int activeAnimations = 0;
    LinkedList<AnimationBundle> animationsBundles = new LinkedList<>();

    protected class AnimationBundle {
        private float x;
        private float y;
        private Figure f;

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


    public void animateMove(MoveInfo i) {

        final Figure actor = i.getActor();
        final ValueAnimator animator = ValueAnimator.ofFloat(0F, 1F);

        float startX = actor.getX() * dx;
        float endX = i.getX() * dx;
        float startY = actor.getY() * dx;
        float endY = i.getY() * dx;

        Float diffX = startX - endX;
        Float diffY = startY - endY;

        float maxDimension;

        if (Math.abs(diffX) > Math.abs(diffY)) maxDimension = diffX;
        else maxDimension = diffY;

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
                        c.drawCircle((x + .5F) * dx, (y + .5F) * dx, dx * .15F, helpPaint);
                    }
                }
        );
    }
}
