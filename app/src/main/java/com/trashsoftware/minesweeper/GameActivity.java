package com.trashsoftware.minesweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import com.trashsoftware.minesweeper.Content.Game;
import com.trashsoftware.minesweeper.GraphContent.GameView;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    private Switch flagSwitch;

    private TextView iconText, timeText, remainingText;

    private int minesRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        int height = intent.getIntExtra("height", 2);
        int width = intent.getIntExtra("width", 2);
        int mines = intent.getIntExtra("mines", 1);
        String title = intent.getStringExtra("title");

        Game game = new Game(height, width, mines);

        gameView = findViewById(R.id.game_view);
        gameView.setGame(game);
        flagSwitch = findViewById(R.id.flagSwitch);
        iconText = findViewById(R.id.game_btn);
        timeText = findViewById(R.id.timeRemaining);
        remainingText = findViewById(R.id.minesRemaining);

        minesRemaining = mines;
        remainingText.setText(String.valueOf(mines));
        timeText.setText("000");
    }

    public boolean isFlagMode() {
        return flagSwitch.isChecked();
    }

    public void showLostIcon() {
        iconText.setText(R.string.sad_icon);
    }

    public void showVictoryIcon() {
        iconText.setText(R.string.happy_icon);
    }

    public void changeMine(int change) {
        minesRemaining -= change;
        remainingText.setText(String.valueOf(minesRemaining));
    }

    public void setMineRemaining(int number) {
        minesRemaining = number;
        remainingText.setText(String.valueOf(minesRemaining));
    }
}
