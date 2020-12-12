package com.robogo.sudokur;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SudokuView extends View {
    private float marginRatio;
    private Paint mLinePaint;
    private Paint mCellPaint;
    private Paint mTextPaint;
    private SudokuBoard sudokuBoard;
    private Point focus;
    private NumPad numPad;

    public SudokuView(Context context) {
        this(context, null);
    }

    public SudokuView(Context context, AttributeSet attrs) {
        super(context, attrs);

        numPad = new NumPad();
        focus = new Point(-1, -1);
        marginRatio = 0.05f;
        mLinePaint = new Paint();
        mCellPaint = new Paint();
        mTextPaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mCellPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setSudokuBoard(SudokuBoard sudokuBoard) {
        this.sudokuBoard = sudokuBoard;
    }

    public SudokuBoard getSudokuBoard() {
        return sudokuBoard;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (sudokuBoard != null && !sudokuBoard.getReadonly()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    onMotionUp(event);
                    break;
                default:
                    return false;
            }
            postInvalidate();
            return true;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int size = Math.min(getWidth(), getHeight());
        float margin = size * marginRatio;
        float gridSize = size - margin * 2;
        float cellSize = gridSize / Sudoku.Size;
        mTextPaint.setTextSize(cellSize * 0.75f);

        fillRect(canvas, margin, margin, gridSize, Color.WHITE);

        if (sudokuBoard != null && sudokuBoard.getInitialized()) {
            drawBoard(canvas, margin, margin, cellSize, sudokuBoard);
        }
        drawGrid(canvas, margin, margin, cellSize, Sudoku.Size, Sudoku.Size / 3);

        // focused
        if (sudokuBoard != null && sudokuBoard.getInitialized()) {
            if (focus.x >= 0 && focus.y >= 0) {
                float x = margin + focus.x * cellSize;
                float y = margin + focus.y * cellSize;
                mLinePaint.setColor(Color.RED);
                mLinePaint.setStrokeWidth(3);
                canvas.drawRect(x, y, x + cellSize, y + cellSize, mLinePaint);
            }
        }

        // num pad
        if (numPad.visible) {
            float x1 = getNumPadPos(numPad.x, margin, cellSize);
            float y1 = getNumPadPos(numPad.y, margin, cellSize);
            fillRect(canvas, x1, y1, cellSize * NumPad.Size, Color.CYAN);
            drawBoard(canvas, x1, y1, cellSize, numPad);
            drawGrid(canvas, x1, y1, cellSize, NumPad.Size, NumPad.Size);
        }
    }

    boolean onMotionUp(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Point pt = getCell(x, y);
        if (pt == null) {
            numPad.hide();
        } else {
            int num = numPad.get(pt);
            if (num > 0) {
                sudokuBoard.setValue(focus.x, focus.y, num);
                numPad.hide();
            } else {
                if (pt.x == focus.x && pt.y == focus.y) {
                    numPad.hide();
                } else {
                    focus.set(pt.x, pt.y);
                    if (sudokuBoard.readonly(pt.x, pt.y)) {
                        numPad.hide();
                    } else {
                        numPad.show(getNumPadIndex(pt.x), getNumPadIndex(pt.y));
                    }
                }
            }
        }
        return true;
    }

    void fillRect(Canvas canvas, float x, float y, float size, int color) {
        mCellPaint.setColor(color);
        canvas.drawRect(x, y, x + size, y + size, mCellPaint);
    }

    void drawGrid(Canvas canvas, float x, float y, float cell, int count, int sub) {
        mLinePaint.setColor(Color.BLACK);
        float x2 = x + cell * count;
        float y2 = y + cell * count;
        for (int i = 0; i <= count; i++) {
            float pos = i * cell;
            mLinePaint.setStrokeWidth(i % sub == 0 ? 2 : 1);
            canvas.drawLine(x, y + pos, x2, y + pos, mLinePaint);
            canvas.drawLine(x + pos, y, x + pos, y2, mLinePaint);
        }
    }

    void drawBoard(Canvas canvas, float x, float y, float cell, Board board) {
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                float xx = x + i * cell;
                float yy = y + j * cell;
                if (board.readonly(i, j)) {
                    mCellPaint.setColor(Color.LTGRAY);
                    canvas.drawRect(xx, yy, xx + cell, yy + cell, mCellPaint);
                }
                int value = board.value(i, j);
                if (value > 0) {
                    xx += cell / 2;
                    yy += cell / 2 - (mTextPaint.ascent() + mTextPaint.descent()) / 2;
                    canvas.drawText(Integer.toString(value), xx, yy, mTextPaint);
                }
            }
        }
    }

    int getNumPadIndex(int n) {
        return n <= 4 ? (n + 1) : (n - 3);
    }

    float getNumPadPos(int n, float margin, float cellSize) {
        return n * cellSize + margin;
    }

    Point getCell(float x, float y) {
        int size = Math.min(getWidth(), getHeight());
        float margin = size * marginRatio;
        if (x < margin || x > size - margin)
            return null;
        if (y < margin || y > size - margin)
            return null;
        x -= margin;
        y -= margin;
        float cellSize = (size - margin - margin) / Sudoku.Size;
        return new Point((int)(x / cellSize), (int)(y / cellSize));
    }

    static class NumPad extends Board {
        static final int Size = 3;
        boolean visible;
        int x;
        int y;

        public void show(int x, int y) {
            this.x = x;
            this.y = y;
            this.visible = true;
        }

        public void hide() {
            this.x = this.y = -1;
            this.visible = false;
        }

        public int get(Point pt) {
            if (visible &&
                pt.x >= x && pt.x < x + Size &&
                pt.y >= y && pt.y < y + Size) {
                return 1 + (pt.x - x) + (pt.y - y) * Size;
            }
            return 0;
        }

        @Override
        public int size() {
            return Size;
        }

        @Override
        public int value(int i, int j) {
            return 1 + i + j * Size;
        }

        @Override
        public boolean readonly(int i, int j) {
            return false;
        }
    }
}