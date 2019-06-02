package com.trashsoftware.minesweeper.GraphContent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.trashsoftware.minesweeper.Content.Game;
import com.trashsoftware.minesweeper.Content.Matrix;
import com.trashsoftware.minesweeper.GameActivity;
import com.trashsoftware.minesweeper.R;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class GameView extends View {

    private Game game;
    private GameActivity parent;

    private static final long MAX_CLICK_DURATION = 200;
    private static final int MAX_CLICK_DISTANCE = 15;

    private long startClickTime;

    private static final int BLOCK_SIZE = 48;
    private static final int MIN_SHOWING = 5;
    private int screenWidth;
    private int screenHeight;

    private Bitmap flagBmp, wrongFlagBmp, explosionBmp, questionMarkBmp;

    private Paint gridPaint, picturePaint, highlightPaint;

    private NumberPaint paint1, paint2, paint3, paint4, paint5, paint6, paint7, paint8;
    private Paint numberBg;

    private float initialScalar;
    private float scalar = 0;
    private boolean scaled;

    private float upLeftX = 0;
    private float upLeftY = 0;

    private float lastTouchX, lastTouchY;
    private int activePointerId;

    private ScaleGestureDetector scaleDetector;

    public GameView(Context context) {
        super(context);

        parent = (GameActivity) context;
        setUp();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        parent = (GameActivity) context;
        setUp();
    }

    private void setUp() {

        gridPaint = new Paint();
        gridPaint.setStrokeWidth(4);
        gridPaint.setColor(Color.BLACK);

        picturePaint = new Paint();

        paint1 = new NumberPaint(Color.BLUE);
        paint2 = new NumberPaint(0xFF00BF00);  // 0,192,0
        paint3 = new NumberPaint(Color.RED);
        paint4 = new NumberPaint(0xFF00000F);  // 0,0,128
        paint5 = new NumberPaint(0xFFF00000);  // 128,0,0
        paint6 = new NumberPaint(0xFF2F4F4F);  // 47,79,79
        paint7 = new NumberPaint(Color.BLACK);
        paint8 = new NumberPaint(Color.DKGRAY);

        numberBg = new Paint();
        numberBg.setColor(Color.LTGRAY);

        highlightPaint = new Paint();
        highlightPaint.setStrokeWidth(5);
        highlightPaint.setStyle(Paint.Style.STROKE);

        flagBmp = BitmapFactory.decodeResource(getResources(), R.drawable.flag);
        wrongFlagBmp = BitmapFactory.decodeResource(getResources(), R.drawable.wrong_flag);
        explosionBmp = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
        questionMarkBmp = BitmapFactory.decodeResource(getResources(), R.drawable.question);

        scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public void setGame(Game game) {
        this.game = game;
    }

    private void setScalar() {
        initialScalar = (float) screenWidth / BLOCK_SIZE / game.getMatrix().getWidth();
        scalar = initialScalar;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        screenWidth = getWidth();
        screenHeight = getHeight();
        if (scalar == 0) setScalar();
        drawGrid(canvas);
        drawBlocks(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);

        int pointerIndex;
        float x;
        float y;

        if (scaled) {
            scaled = false;
            pointerIndex = event.getActionIndex();
            lastTouchX = event.getX(pointerIndex);
            lastTouchY = event.getY(pointerIndex);
            return true;
        }

        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startClickTime = System.currentTimeMillis();

                pointerIndex = event.getActionIndex();
                x = event.getX(pointerIndex);
                y = event.getY(pointerIndex);

                lastTouchX = x;
                lastTouchY = y;

                activePointerId = event.getPointerId(0);

                int[] rc = getRowColByClickPos(lastTouchX, lastTouchY);
                game.pressDown(rc[0], rc[1]);
                break;
            case MotionEvent.ACTION_MOVE:
//                game.getMatrix().releaseAll();
                pointerIndex = event.findPointerIndex(activePointerId);

                x = event.getX(pointerIndex);
                y = event.getY(pointerIndex);

                final float dx = x - lastTouchX;
                final float dy = y - lastTouchY;

                upLeftX += dx;
                upLeftY += dy;

                restoreOutBounds();

                invalidate();

                lastTouchX = x;
                lastTouchY = y;
                break;
            case MotionEvent.ACTION_UP:
                game.getMatrix().releaseAll();
                pointerIndex = event.findPointerIndex(activePointerId);

                x = event.getX(pointerIndex);
                y = event.getY(pointerIndex);

                // TODO: Bug
                if (System.currentTimeMillis() - startClickTime < MAX_CLICK_DURATION &&
                        Math.abs(x - lastTouchX) < MAX_CLICK_DISTANCE &&
                        Math.abs(y - lastTouchY) < MAX_CLICK_DISTANCE) {
                    performClick();
                }

                activePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_CANCEL:
                game.getMatrix().releaseAll();
                activePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                game.getMatrix().releaseAll();
                pointerIndex = event.getActionIndex();
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == activePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    lastTouchX = event.getX(newPointerIndex);
                    lastTouchY = event.getY(newPointerIndex);
                    activePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            default:
                game.getMatrix().releaseAll();
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        if (!game.isLost() && !game.isWin()) {
            int[] rc = getRowColByClickPos(lastTouchX, lastTouchY);

            if (parent.isFlagMode()) {
                int res = game.flagClick(rc[0], rc[1]);
                parent.changeMine(res);
            } else {
                game.footClick(rc[0], rc[1]);
            }

            invalidate();

            if (game.isLost()) {
                parent.showLostIcon();
            }
            if (game.isWin()) {
                parent.showVictoryIcon();
                parent.setMineRemaining(0);
            }
        }
        return super.performClick();
    }

    private int[] getRowColByClickPos(float clickX, float clickY) {
        int row = (int) ((clickY - upLeftY) / scalar / BLOCK_SIZE);
        int col = (int) ((clickX - upLeftX) / scalar / BLOCK_SIZE);
        return new int[]{row, col};
    }

    private void restoreOutBounds() {
        if (upLeftY > 0) upLeftY = 0;
        if (upLeftX > 0) upLeftX = 0;

        float desiredX = getDesiredUpLeftX();
        float desiredY = getDesiredUpLeftY();

        if (upLeftX < desiredX) upLeftX = desiredX;
        if (upLeftY < desiredY) upLeftY = desiredY;
    }

    private float getDesiredUpLeftX() {
        int cols = game.getMatrix().getWidth();
        return screenWidth - (cols * BLOCK_SIZE * scalar);
    }

    private float getDesiredUpLeftY() {
        int rows = game.getMatrix().getHeight();
        float currentHeightPixel = rows * BLOCK_SIZE * scalar;
        if (currentHeightPixel < screenHeight) return 0;
        else {
            return screenHeight - currentHeightPixel;
        }
    }

    private void drawGrid(Canvas canvas) {
        int vLines = game.getMatrix().getHeight() + 1;
        int hLines = game.getMatrix().getWidth() + 1;

        float lastHLinePos = (vLines - 1) * BLOCK_SIZE * scalar + upLeftY;

        for (int r = 0; r < vLines; r++) {
            float linePos = r * BLOCK_SIZE * scalar + upLeftY;
            canvas.drawLine(0, linePos, screenWidth, linePos, gridPaint);
        }

        for (int c = 0; c < hLines; c++) {
            float linePos = c * BLOCK_SIZE * scalar + upLeftX;
            canvas.drawLine(linePos, 0, linePos, lastHLinePos, gridPaint);
        }
    }

    private void drawBlocks(Canvas canvas) {
        for (int r = 0; r < game.getMatrix().getHeight(); r++) {
            for (int c = 0; c < game.getMatrix().getWidth(); c++) {
                drawBlock(r, c, canvas);
            }
        }
    }

    private void drawBlock(int r, int c, Canvas canvas) {
        int rep = game.getMatrix().getItemAt(r, c);
        float beginY = r * BLOCK_SIZE * scalar + upLeftY;
        float beginX = c * BLOCK_SIZE * scalar + upLeftX;
        float endY = beginY + BLOCK_SIZE * scalar;
        float endX = beginX + BLOCK_SIZE * scalar;
        switch (rep) {
            case Matrix.UNOPENED:
                break;
            case Matrix.FLAG:
                drawPicture(flagBmp, beginX, beginY, canvas);
                break;
            case Matrix.QUESTION_MARK:
                drawPicture(questionMarkBmp, beginX, beginY, canvas);
                break;
            case Matrix.EXPLOSIVE:
                drawPicture(explosionBmp, beginX, beginY, canvas);
                break;
            case Matrix.WRONG_FLAG:
                drawPicture(wrongFlagBmp, beginX, beginY, canvas);
                break;
            case Matrix.PRESSED:
                drawHighlight(beginX, beginY, endX, endY, canvas);
                break;
            default:
                break;
            case 0:
                drawNumberBackground(beginX, beginY, endX, endY, canvas);
                break;
            case 1:
                drawNumber(1, beginX, beginY, endX, endY, paint1, canvas);
                break;
            case 2:
                drawNumber(2, beginX, beginY, endX, endY, paint2, canvas);
                break;
            case 3:
                drawNumber(3, beginX, beginY, endX, endY, paint3, canvas);
                break;
            case 4:
                drawNumber(4, beginX, beginY, endX, endY, paint4, canvas);
                break;
            case 5:
                drawNumber(5, beginX, beginY, endX, endY, paint5, canvas);
                break;
            case 6:
                drawNumber(6, beginX, beginY, endX, endY, paint6, canvas);
                break;
            case 7:
                drawNumber(7, beginX, beginY, endX, endY, paint7, canvas);
                break;
            case 8:
                drawNumber(8, beginX, beginY, endX, endY, paint8, canvas);
                break;
        }
    }

    private void drawNumberBackground(float beginX, float beginY, float endX, float endY,
                                      Canvas canvas) {
        canvas.drawRect(beginX + 2, beginY + 2, endX - 2, endY - 2, numberBg);
    }

    private void drawHighlight(float beginX, float beginY, float endX, float endY,
                               Canvas canvas) {
        canvas.drawRect(beginX + 4, beginY + 4, endX - 4, endY - 4, highlightPaint);
    }

    private void drawNumber(int number, float beginX, float beginY, float endX, float endY,
                            NumberPaint paint, Canvas canvas) {
        drawNumberBackground(beginX, beginY, endX, endY, canvas);
        paint.setTextSize(30 * scalar);
        canvas.drawText(String.valueOf(number),
                beginX + BLOCK_SIZE * scalar / 2,
                beginY + (20 + BLOCK_SIZE) * scalar / 2,
                paint);
    }

    private void drawPicture(Bitmap bmp, float x, float y, Canvas canvas) {
        int bmpScale = (int) (BLOCK_SIZE * scalar) - 4;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, bmpScale, bmpScale, true);

        canvas.drawBitmap(scaledBitmap, x + 2, y + 2, picturePaint);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            scaleFactor = (float) Math.max(0.1, Math.min(10, scaleFactor));
            scalar *= scaleFactor;
            scalar = Math.max(initialScalar,
                    Math.min(scalar, (float) screenWidth / BLOCK_SIZE / MIN_SHOWING));
            final float focusX = detector.getFocusX();
            final float focusY = detector.getFocusY();
            final float focusXOffset = (upLeftX - focusX) * scaleFactor;
            final float focusYOffset = (upLeftY - focusY) * scaleFactor;
            upLeftX = focusX + focusXOffset;
            upLeftY = focusY + focusYOffset;
            scaled = true;

            restoreOutBounds();

            invalidate();
            return true;
        }
    }
}

class NumberPaint extends Paint {

    NumberPaint(int color) {
        super();

        setColor(color);
        setTextAlign(Align.CENTER);
    }
}
