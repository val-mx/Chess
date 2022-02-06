package valmx.nelly.chess.managers;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import valmx.nelly.chess.R;

public class DrawableManager {
    public Drawable BLACK_KNIGHT;
    public Drawable BLACK_KING;
    public Drawable BLACK_QUEEN;
    public Drawable BLACK_PAWN;
    public Drawable BLACK_BISHOP;
    public Drawable BLACK_ROOK;


    public Drawable WHITE_KNIGHT;
    public Drawable WHITE_KING;
    public Drawable WHITE_QUEEN;
    public Drawable WHITE_PAWN;
    public Drawable WHITE_BISHOP;
    public Drawable WHITE_ROOK;

    @SuppressLint("UseCompatLoadingForDrawables")
    public DrawableManager(Resources res) {

        BLACK_BISHOP = res.getDrawable(R.drawable.bishop_black);
        BLACK_PAWN = res.getDrawable(R.drawable.pawn_black);
        BLACK_KING = res.getDrawable(R.drawable.king_black);
        BLACK_QUEEN = res.getDrawable(R.drawable.queen_black);
        BLACK_KNIGHT = res.getDrawable(R.drawable.night_black);
        BLACK_ROOK = res.getDrawable(R.drawable.rook_black);

        WHITE_BISHOP = res.getDrawable(R.drawable.bishop_white);
        WHITE_PAWN = res.getDrawable(R.drawable.pawn_white);
        WHITE_KING = res.getDrawable(R.drawable.king_white);
        WHITE_QUEEN = res.getDrawable(R.drawable.queen_white);
        WHITE_KNIGHT = res.getDrawable(R.drawable.night_white);
        WHITE_ROOK = res.getDrawable(R.drawable.rook_white);

    }

}
