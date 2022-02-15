package valmx.nelly.chess;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class AnimatedTextView extends AppCompatTextView {
    private String text = null;


    public AnimatedTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
