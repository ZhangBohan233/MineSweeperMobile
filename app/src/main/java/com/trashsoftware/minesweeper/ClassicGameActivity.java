package com.trashsoftware.minesweeper;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.trashsoftware.minesweeper.Content.DataManager;
import com.trashsoftware.minesweeper.GraphContent.MsDialogFragment;

import java.util.Map;

public class ClassicGameActivity extends AppCompatActivity {

    private EditText minesText, heightText, widthText;

    private TextView easyRecordText, mediumRecordText, hardRecordText;

    private MsDialogFragment dialogFragment = new MsDialogFragment();

    private static final int SIZE_LIMIT = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_game);

        minesText = findViewById(R.id.minesText);
        heightText = findViewById(R.id.heightText);
        widthText = findViewById(R.id.widthText);

        easyRecordText = findViewById(R.id.easyRecord);
        mediumRecordText = findViewById(R.id.mediumRecord);
        hardRecordText = findViewById(R.id.hardRecord);

        showRecords();
    }

    private void showRecords() {
        Map<String, Long> records = DataManager.getRecords(this);
        Long er, mr, hr;
        er = records.get("Easy");
        mr = records.get("Medium");
        hr = records.get("Hard");
        if (er != null) {
            easyRecordText.setText(getRecordShowingTime(er));
        }
        if (mr != null) {
            mediumRecordText.setText(getRecordShowingTime(mr));
        }
        if (hr != null) {
            hardRecordText.setText(getRecordShowingTime(hr));
        }
    }

    private String getRecordShowingTime(long ms) {
        double s = (double) ms / 1000;
        return String.format(getResources().getConfiguration().locale,
                "%.2f %s",
                s,
                getString(R.string.seconds));
    }

    public void startEasyGame(View view) {
        showGame(9, 9, 10, "Easy");
    }

    public void startMediumGame(View view) {
        showGame(16, 16, 40, "Medium");
    }

    public void startHardGame(View view) {
        showGame(30, 24, 120, "Hard");
    }

    public void startCustomGame(View view) {
        String minesStr = minesText.getText().toString();
        String heightStr = heightText.getText().toString();
        String widthStr = widthText.getText().toString();

        try {
            int height = Integer.valueOf(heightStr);
            int width = Integer.valueOf(widthStr);
            int mines = Integer.valueOf(minesStr);

            if (checkSizeMinesRange(height, width, mines))
                showGame(height, width, mines, "Custom");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private boolean checkSizeMinesRange(int height, int width, int mines) {
        if (height > SIZE_LIMIT || width > SIZE_LIMIT || height < 2 || width < 2) {
            String msg = String.format(getResources().getConfiguration().locale,
                    "%s %d ~ %d",
                    getString(R.string.size_range),
                    2, SIZE_LIMIT
            );
            dialogFragment.show(getSupportFragmentManager(), msg);
            return false;
        }
        int minesLimit = height * width - 2;
        if (mines > minesLimit || mines < 2) {
            String msg = String.format(getResources().getConfiguration().locale,
                    "%s %d ~ %d",
                    getString(R.string.mines_range),
                    2, minesLimit
            );
            dialogFragment.show(getSupportFragmentManager(), msg);
            return false;
        }
        return true;
    }

    private void showGame(int height, int width, int mines, String title) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("height", height);
        intent.putExtra("width", width);
        intent.putExtra("mines", mines);
        intent.putExtra("title", title);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            showRecords();
        }
    }
}
