package com.trashsoftware.minesweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.trashsoftware.minesweeper.Content.Game;
import com.trashsoftware.minesweeper.GraphContent.GameView;

public class GameActivity extends AppCompatActivity {

    private int height, width, totalMines;

    private GameView gameView;

    private Switch flagSwitch;

    private TextView remainingText;

    TextView timeText;

    private Button gameButton;

    private int minesRemaining;

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        int height = intent.getIntExtra("height", 2);
        int width = intent.getIntExtra("width", 2);
        int mines = intent.getIntExtra("mines", 1);
        String title = intent.getStringExtra("title");

        this.height = height;
        this.width = width;
        this.totalMines = mines;

        gameView = findViewById(R.id.game_view);

        flagSwitch = findViewById(R.id.flagSwitch);
        gameButton = findViewById(R.id.game_btn);
        timeText = findViewById(R.id.timeRemaining);
        remainingText = findViewById(R.id.minesRemaining);

        newGame();
    }

    private void newGame() {
        Game game = new Game(height, width, totalMines);
        gameView.setGame(game);

        minesRemaining = totalMines;
        gameView.invalidate();
        if (timer != null) {
            stopTimer();
        }
        timer = new Timer(this, timeText);
        timeText.setText("0");
        remainingText.setText(String.valueOf(totalMines));
        gameButton.setText(R.string.gaming_icon);
        flagSwitch.setChecked(false);
    }

    public void startTimer() {
        timer.start();
    }

    public double stopTimer() {
        return timer.stop();
    }

    public boolean isFlagMode() {
        return flagSwitch.isChecked();
    }

    private void showLostIcon() {
        gameButton.setText(R.string.sad_icon);
    }

    private void showVictoryIcon() {
        gameButton.setText(R.string.happy_icon);
    }

    public void changeMine(int change) {
        minesRemaining -= change;
        remainingText.setText(String.valueOf(minesRemaining));
    }

    public void setMineRemaining(int number) {
        minesRemaining = number;
        remainingText.setText(String.valueOf(minesRemaining));
    }

    public void restartGame(View view) {
        newGame();
    }

    public void victory() {
        showVictoryIcon();
        double timeUsed = stopTimer();
    }

    public void lost() {
        showLostIcon();
        timer.stop();
    }
}


class Timer {

    private long startTime;

    private boolean running;

    private TextView textView;

    private TimerProcess process;

    private GameActivity parent;

    Timer(GameActivity parent, TextView textView) {
        this.textView = textView;
        this.parent = parent;
    }

    void start() {
        running = true;
        startTime = System.currentTimeMillis();
        process = new TimerProcess();
        Thread thread = new Thread(process);
        thread.start();
    }

    double stop() {
        running = false;
        long stopTime = System.currentTimeMillis();
        return (double) (stopTime - startTime) / 1000;
    }

    private class TimerProcess implements Runnable {
        @Override
        public void run() {
            try {
                while (running) {
                    Thread.sleep(100);
                    updateTimer();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTimer() {
        long current = System.currentTimeMillis();
        final long diff = (current - startTime) / 1000;
        parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parent.timeText.setText(String.valueOf(diff));
            }
        });
    }
}
