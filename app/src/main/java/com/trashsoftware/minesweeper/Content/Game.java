package com.trashsoftware.minesweeper.Content;

public class Game {

    private Matrix matrix;

    private boolean lost;

    private boolean won;

    public Game(int height, int width, int mines) {
        matrix = new Matrix(height, width);
        matrix.initialize(mines);
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public int flagClick(int r, int c) {
        int res = 0;  // the change of number of flags

        switch (matrix.getItemAt(r, c)) {
            case Matrix.UNOPENED:
                matrix.putFlag(r, c);
                res = 1;
                break;
            case Matrix.FLAG:
                matrix.putQuestion(r, c);
                res = -1;
                break;
            case Matrix.QUESTION_MARK:
                matrix.restoreUnopened(r, c);
                break;
            default:
                showSurround(r, c);
                break;
        }

        checkWin();
        return res;
    }

    public void footClick(int r, int c) {
        if (matrix.getItemAt(r, c) == Matrix.UNOPENED) {
            // Open an unopened field
            recursiveShow(r, c);
        } else if (matrix.getItemAt(r, c) > 0) {
            // Press an
            showSurround(r, c);
        }

        checkWin();
    }

    public void pressDown(int r, int c) {
        if (matrix.getItemAt(r, c) > 0) {
            for (int r1 = r - 1; r1 <= r + 1; r1++) {
                for (int c1 = c - 1; c1 <= c + 1; c1++) {
                    if (inBound(r1, c1) && (r1 != r || c1 != c) &&
                            matrix.getItemAt(r1, c1) == Matrix.UNOPENED) {
                        matrix.press(r1, c1);
                    }
                }
            }
        }
    }

    private void lose() {
        showAllMines();
        lost = true;
    }

    private void showOne(int r, int c) {
        if (matrix.isMine(r, c)) {
            lose();
            return;
        }
        int count = 0;
        for (int r1 = r - 1; r1 <= r + 1; r1++) {
            for (int c1 = c - 1; c1 <= c + 1; c1++) {
                if (r1 >= 0 &&
                        r1 < matrix.getHeight() &&
                        c1 >= 0 &&
                        c1 < matrix.getWidth() &&
                        (r1 != r || c1 != c)) {
                    if (matrix.isMine(r1, c1)) count++;
                }
            }
        }
        matrix.setNumber(r, c, count);
    }

    private boolean inBound(int r, int c) {
        return r >= 0 && r < matrix.getHeight() && c >= 0 && c < matrix.getWidth();
    }

    private void showSurround(int r, int c) {
        if (matrix.getItemAt(r, c) > 0) {
            if (checkSurroundCorrect(r, c)) {
                for (int r1 = r - 1; r1 <= r + 1; r1++) {
                    for (int c1 = c - 1; c1 <= c + 1; c1++) {
                        if (r1 != r || c1 != c) {
                            recursiveShow(r1, c1);
                        }
                    }
                }
            }
        }
    }

    private void recursiveShow(int r, int c) {
        if (inBound(r, c)) {
            if (matrix.getItemAt(r, c) == Matrix.UNOPENED) {
                showOne(r, c);
                if (matrix.getItemAt(r, c) == 0) {
                    for (int r1 = r - 1; r1 <= r + 1; r1++) {
                        for (int c1 = c - 1; c1 <= c + 1; c1++) {
                            if (r1 != r || c1 != c) {
                                recursiveShow(r1, c1);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean checkSurroundCorrect(int r, int c) {
        int number = matrix.getItemAt(r, c);
        if (number == 0) return true;

        boolean res = true;

        for (int r1 = r - 1; r1 <= r + 1; r1++) {
            for (int c1 = c - 1; c1 <= c + 1; c1++) {
                if (inBound(r1, c1) && (r1 != r || c1 != c)) {
                    if (matrix.isMine(r1, c1) && matrix.getItemAt(r1, c1) != Matrix.FLAG)
                        res = false;
                    if (!matrix.isMine(r1, c1) && matrix.getItemAt(r1, c1) == Matrix.FLAG) {
                        markWrongFlag(r1, c1);
                        return false;
                    }
                }
            }
        }
        return res;
    }

    private void markWrongFlag(int r, int c) {
        matrix.putWrongFlag(r, c);
        lose();
    }

    private void checkWin() {
        int unopenedCount = 0;
        int hiddenMinesCount = 0;
        for (int r = 0; r < matrix.getHeight(); r++) {
            for (int c = 0; c < matrix.getWidth(); c++) {
                if (matrix.getItemAt(r, c) == Matrix.UNOPENED)
                    unopenedCount++;
                if (matrix.isMine(r, c)) {
                    if (matrix.getItemAt(r, c) == Matrix.FLAG) continue;

                    hiddenMinesCount++;
                }
            }
        }

        if (unopenedCount == hiddenMinesCount) {
            won = true;
            showAllResult();
        }
    }

    private void showAllResult() {
        for (int r = 0; r < matrix.getHeight(); r++) {
            for (int c = 0; c < matrix.getWidth(); c++) {
                if (matrix.getItemAt(r, c) == Matrix.UNOPENED)
                    matrix.putFlag(r, c);
            }
        }
    }

    public boolean isWin() {
        return won;
    }

    public boolean isLost() {
        return lost;
    }

    private void showAllMines() {
        for (int r = 0; r < matrix.getHeight(); r++) {
            for (int c = 0; c < matrix.getWidth(); c++) {
                if (matrix.getItemAt(r, c) == Matrix.UNOPENED && matrix.isMine(r, c)) {
                    matrix.putExplosive(r, c);
                }
            }
        }
    }
}
