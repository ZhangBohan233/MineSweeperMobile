package com.trashsoftware.minesweeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ClassicGameActivity extends AppCompatActivity {

    private EditText minesText, heightText, widthText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_game);

        minesText = findViewById(R.id.minesText);
        heightText = findViewById(R.id.heightText);
        widthText = findViewById(R.id.widthText);
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
        String mines = minesText.getText().toString();
        String height = heightText.getText().toString();
        String width = widthText.getText().toString();

        try {
            showGame(Integer.valueOf(height), Integer.valueOf(width), Integer.valueOf(mines), "");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void showGame(int height, int width, int mines, String title) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("height", height);
        intent.putExtra("width", width);
        intent.putExtra("mines", mines);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}
