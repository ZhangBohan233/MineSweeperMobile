package com.trashsoftware.minesweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.trashsoftware.minesweeper.Content.DataManager;
import com.trashsoftware.minesweeper.Content.Game;
import com.trashsoftware.minesweeper.GraphContent.GameView;
import com.trashsoftware.minesweeper.GraphContent.MsDialogFragment;

import java.io.IOException;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    private int height, width, totalMines;

    private String gameName;

    private GameView gameView;

    private Switch flagSwitch;

    private TextView remainingText;

    TextView timeText;

    private Button gameButton;

    private int minesRemaining;

    private Timer timer;

    private MsDialogFragment dialogFragment = new MsDialogFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        int height = intent.getIntExtra("height", 2);
        int width = intent.getIntExtra("width", 2);
        int mines = intent.getIntExtra("mines", 1);
        gameName = intent.getStringExtra("title");

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
        timer = new Timer(this);
        timeText.setText("0");
        remainingText.setText(String.valueOf(totalMines));
        gameButton.setText(R.string.gaming_icon);
        flagSwitch.setChecked(false);
    }

    public void startTimer() {
        timer.start();
    }

    public long stopTimer() {
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
        long time = stopTimer();
        if (!gameName.equals("Custom")) {
            try {
                DataManager.updateRecord(this, gameName, time);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        showWinDialog(time);
    }

    public void lost() {
        showLostIcon();
        timer.stop();
    }

    private void showWinDialog(long timeUsed) {
        double dt = (double) timeUsed / 1000;
        String msg = String.format(getResources().getConfiguration().locale,
                "%s\n%s %s: %.2f %s",
                getString(R.string.happy_icon),
                getString(R.string.success),
                getString(R.string.time_used),
                dt,
                getString(R.string.seconds));
        dialogFragment.show(this.getSupportFragmentManager(), msg);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        setResult(RESULT_CANCELED);
        finish();
    }
}


class Timer {

    private long startTime;

    private boolean running;

//    private TextView textView;

    private GameActivity parent;

    Timer(GameActivity parent) {
//        this.textView = textView;
        this.parent = parent;
    }

    void start() {
        running = true;
        startTime = System.currentTimeMillis();
        TimerProcess process = new TimerProcess();
        Thread thread = new Thread(process);
        thread.start();
    }

    long stop() {
        running = false;
        long stopTime = System.currentTimeMillis();
        return stopTime - startTime;
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
