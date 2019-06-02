package com.trashsoftware.minesweeper.Content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Matrix {

    private int height, width;

    public static final int UNOPENED = -1;
    public static final int FLAG = -2;
    public static final int QUESTION_MARK = -3;
    public static final int EXPLOSIVE = -4;
    public static final int WRONG_FLAG = -5;
    public static final int PRESSED = -6;

    private boolean[][] mineMatrix;  // false for empty, true for mine

    private int flagMatrix[][];  // 0 - 8 for numbers, -1 for unopened, -2 for flag,
    // -3 for question mark, -4 for mine detonated

    public Matrix(int height, int width) {
        this.height = height;
        this.width = width;
        this.mineMatrix = new boolean[height][width];
        this.flagMatrix = new int[height][width];
        for (int r = 0; r < height; r++)
            for (int c = 0; c < width; c++)
                flagMatrix[r][c] = -1;
    }

    void initialize(int mines) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < height * width; i++) {
            list.add(i);
        }

        for (int i = 0; i < mines; i++) {
            int index = new Random().nextInt(list.size());
            int position = list.remove(index);
            int row = position / width;
            int col = position % width;
            mineMatrix[row][col] = true;
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean isMine(int r, int c) {
        return mineMatrix[r][c];
    }

    public int getItemAt(int r, int c) {
        return flagMatrix[r][c];
    }

    void restoreUnopened(int r, int c) {
        flagMatrix[r][c] = UNOPENED;
    }

    void putFlag(int r, int c) {
        flagMatrix[r][c] = FLAG;
    }

    void putQuestion(int r, int c) {
        flagMatrix[r][c] = QUESTION_MARK;
    }

    void setNumber(int r, int c, int number) {
        flagMatrix[r][c] = number;
    }

    void putExplosive(int r, int c) {
        flagMatrix[r][c] = EXPLOSIVE;
    }

    void putWrongFlag(int r, int c) {
        flagMatrix[r][c] = WRONG_FLAG;
    }

    void press(int r, int c) {
        flagMatrix[r][c] = PRESSED;
    }

    public void releaseAll() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (flagMatrix[r][c] == PRESSED) {
                    flagMatrix[r][c] = UNOPENED;
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < height; r++) {
            sb.append(Arrays.toString(flagMatrix[r])).append('\n');
        }
        return sb.toString();
    }
}
