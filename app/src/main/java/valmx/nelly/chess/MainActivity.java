package valmx.nelly.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ChessView.ChessListener {

    private TextView infoText = null;
    private ChessView chessView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoText = findViewById(R.id.info);
        chessView = findViewById(R.id.chessView);


        chessView.setListener(this);

    }

    @Override
    public void onNewRound(ChessBoard board) {

        if (!board.getPlayer()) {
            infoText.setText("White to move...");
        } else
            infoText.setText("Black thinking...");

        if (board.isKingInCheck() && !board.getPlayer()) {
            if (board.getPlayer())
                infoText.setText("White In Check...");
            else
                infoText.setText("Black In Check...");
        }

    }
}