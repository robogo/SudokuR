package com.robogo.sudokur;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.plattysoft.leonids.ParticleSystem;

public class SudokuView extends View {
    private static final int REGION_WIDTH = 3;
    private final Paint mLinePaint;
    private final Paint mCellPaint;
    private final Paint mTextPaint;
    private final Drawable icLock;
    private final Drawable icOpen;
    private final Drawable icFlag;
    private final Drawable icDel;
    private SudokuBoard sudokuBoard;
    private Cell focus;
    private NumPad numPad;

    public SudokuView(Context context) {
        this(context, null);
    }

    public SudokuView(Context context, AttributeSet attrs) {
        super(context, attrs);

        numPad = new NumPad(this);
        focus = new Cell(-1, -1);
        mLinePaint = new Paint();
        mCellPaint = new Paint();
        mTextPaint = new Paint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mCellPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        Resources res = getResources();
        icLock = res.getDrawable(R.drawable.ic_lock, getContext().getTheme());
        icOpen = res.getDrawable(R.drawable.ic_unlock, getContext().getTheme());
        icFlag = res.getDrawable(R.drawable.ic_flag, getContext().getTheme());
        icDel = res.getDrawable(R.drawable.ic_del, getContext().getTheme());
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
        if (sudokuBoard != null && sudokuBoard.initialized()) {
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

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(getWidth(), getHeight());
        float cellSize = size / Sudoku.Size;
        mTextPaint.setTextSize(cellSize * 0.75f);

        fillRect(canvas, 0, 0, width, height, Color.DKGRAY);
        fillRect(canvas, 0, 0, size, size, Color.WHITE);

        if (sudokuBoard != null && sudokuBoard.initialized()) {
            drawBoard(canvas, 0, 0, cellSize);
        }

        drawGrid(canvas, 0, 0, cellSize, Sudoku.Size, Sudoku.Size, Sudoku.Size / 3);

        // focused
        if (sudokuBoard != null && sudokuBoard.initialized()) {
            if (focus.row >= 0 && focus.col >= 0) {
                float x = focus.col * cellSize;
                float y = focus.row * cellSize;
                mLinePaint.setColor(Color.RED);
                mLinePaint.setStrokeWidth(3);
                canvas.drawRect(x, y, x + cellSize, y + cellSize, mLinePaint);
            }
        }

        // num pad
        if (numPad.visible) {
            float x1 = getNumPadPos(numPad.left, cellSize);
            float y1 = getNumPadPos(numPad.top, cellSize);
            fillRect(canvas, x1, y1, cellSize * numPad.col(), cellSize * numPad.row(), Color.CYAN);
            drawNumPad(canvas, x1, y1, cellSize);
            drawGrid(canvas, x1, y1, cellSize, numPad.row(), numPad.col(),99);
        }
    }

    private boolean onMotionUp(MotionEvent event) {
        // x and pt.x are translated to col, as y/pt.y to row
        float x = event.getX();
        float y = event.getY();
        Cell cell = getCell(x, y);
        if (cell == null) {
            numPad.hide();
        } else {
            int num = numPad.get(cell.row, cell.col);
            Log.i("VIEW", String.format("pad:%d,%d cell:%d,%d, num:%d", numPad.left, numPad.top, cell.row, cell.col, num));
            switch (num) {
            case NumPad.LOCK:
                sudokuBoard.lock(focus.row, focus.col, !sudokuBoard.locked(focus.row, focus.col));
                numPad.hide();
                break;
            case NumPad.CLEAR:
                sudokuBoard.clear(focus.row, focus.col);
                numPad.hide();
                break;
            case NumPad.FLAG:
                sudokuBoard.flag(focus.row, focus.col, !sudokuBoard.flagged(focus.row, focus.col));
                numPad.hide();
                break;
            default:
                if (num > 0) {
                    numPad.hide();
                    if (!sudokuBoard.locked(focus.row, focus.col)) {
                        boolean done = sudokuBoard.set(focus.row, focus.col, num);
                        if (done) congrad();
                    }
                } else {
                    if (cell.row == focus.row && cell.col == focus.col && numPad.visible) {
                        numPad.hide();
                    } else {
                        focus.set(cell.row, cell.col);
                        if (sudokuBoard.readonly(cell.row, cell.col)) {
                            numPad.hide();
                        } else {
                            int rr = getNumPadIndex(cell.row, numPad.row());
                            int cc = getNumPadIndex(cell.col, numPad.col());
                            numPad.show(rr, cc, !sudokuBoard.locked(rr, cc));
                            Log.i("VIEW", String.format("cell:%d,%d pad:%d,%d", cell.row, cell.col, rr, cc));
                        }
                    }
                }
                break;
            }
        }
        return true;
    }

    private void fillRect(Canvas canvas, float x, float y, float width, float height, int color) {
        mCellPaint.setColor(color);
        canvas.drawRect(x, y, x + width, y + height, mCellPaint);
    }

    private void drawGrid(Canvas canvas, float x, float y, float cell, int row, int col, int sub) {
        mLinePaint.setColor(Color.BLACK);
        float x2 = x + cell * col;
        for (int i = 0; i <= col; i++) {
            float pos = y + i * cell;
            mLinePaint.setStrokeWidth(i % sub == 0 ? REGION_WIDTH : 1);
            canvas.drawLine(x, pos, x2, pos, mLinePaint);
        }
        float y2 = y + cell * row;
        for (int i = 0; i <= row; i++) {
            float pos = x + i * cell;
            mLinePaint.setStrokeWidth(i % sub == 0 ? REGION_WIDTH : 1);
            canvas.drawLine(pos, y, pos, y2, mLinePaint);
        }
    }

    private void drawBoard(Canvas canvas, float x, float y, float cell) {
        for (int i = 0; i < sudokuBoard.row(); i++) {
            for (int j = 0; j < sudokuBoard.col(); j++) {
                float xx = x + j * cell;
                float yy = y + i * cell;
                if (sudokuBoard.readonly(i, j)) {
                    mCellPaint.setColor(Color.LTGRAY);
                    canvas.drawRect(xx, yy, xx + cell, yy + cell, mCellPaint);
                }
                int icx = (int)xx;
                int icy = (int)yy;
                int ics = (int)(cell / 2);
                if (sudokuBoard.locked(i, j)) {
                    icLock.setBounds(icx, icy, icx + ics, icy + ics);
                    icLock.draw(canvas);
                    icy += ics;
                }
                if (sudokuBoard.flagged(i, j)) {
                    icFlag.setBounds(icx, icy, icx + ics, icy + ics);
                    icFlag.draw(canvas);
                }
                int value = sudokuBoard.value(i, j);
                if (value > 0) {
                    xx += cell / 2;
                    yy += cell / 2 - (mTextPaint.ascent() + mTextPaint.descent()) / 2;
                    mTextPaint.setColor(sudokuBoard.conflicting(i, j) ? Color.RED : Color.BLACK);
                    canvas.drawText(Integer.toString(value), xx, yy, mTextPaint);
                }
            }
        }
    }

    private void drawNumPad(Canvas canvas, float x, float y, float cell) {
        for (int i = 0; i < numPad.row(); i++) {
            for (int j = 0; j < numPad.col(); j++) {
                float xx = x + j * cell;
                float yy = y + i * cell;
                int value = numPad.value(i, j);
                if (value < NumPad.LOCK) {
                    xx += cell / 2;
                    yy += cell / 2 - (mTextPaint.ascent() + mTextPaint.descent()) / 2;
                    canvas.drawText(Integer.toString(value), xx, yy, mTextPaint);
                } else {
                    Drawable d;
                    if (value == NumPad.LOCK)
                        d = sudokuBoard.locked(focus.row, focus.col) ? icOpen : icLock;
                    else if (value == NumPad.CLEAR)
                        d = icDel;
                    else
                        d = icFlag;
                    d.setBounds((int)xx, (int)yy, (int)(xx + cell), (int)(yy + cell));
                    d.draw(canvas);
                }
            }
        }
    }

    private int getNumPadIndex(int n, int max) {
        return n <= max ? (n + 1) : (n - max);
    }

    private float getNumPadPos(int n, float cellSize) {
        return n * cellSize;
    }

    private Cell getCell(float x, float y) {
        int size = Math.min(getWidth(), getHeight());
        if (x < 0 || x > size)
            return null;
        if (y < 0 || y > size)
            return null;
        float cellSize = size / Sudoku.Size;
        return new Cell((int)(y / cellSize), (int)(x / cellSize));
    }

    private void congrad() {
        new ParticleSystem((Activity) getContext(), 200, R.drawable.ic_flower, 5000)
                .setSpeedRange(0.2f, 0.5f)
                .oneShot(this, 50);
    }

    static class Cell {
        int row;
        int col;
        public Cell(int r, int c) {
            set(r, c);
        }
        public void set(int r, int c) {
            row = r;
            col = c;
        }
    }

    static class NumPad extends Board {
        static final int LOCK = 10;
        static final int FLAG = 11;
        static final int CLEAR = 12;
        private final SudokuView parent;
        int left;
        int top;
        boolean showNumbers;
        boolean visible;

        public NumPad(SudokuView parent) {
            this.parent = parent;
            showNumbers = true;
        }

        public void show(int row, int col, boolean number) {
            this.top = row;
            this.left = col;
            this.showNumbers = number;
            this.visible = true;
        }

        public void hide() {
            this.top = this.left = -1;
            this.visible = false;
        }

        public int get(int row, int col) {
            if (visible &&
                col >= left && col < left + col() &&
                row >= top && row < top + row()) {
                int index = 1 + (row - top) * col() + (col - left);
                if (!showNumbers)
                    index += LOCK;
                return index;
            }
            return 0;
        }

        @Override
        public int row() {
            return showNumbers ? 4 : 1;
        }

        @Override
        public int col() { return 3; }

        @Override
        public int value(int i, int j) {
            int numRows = showNumbers ? 3 : 0;
            if (i == numRows) {
                if (j == 0) return LOCK;
                if (j == 1) return FLAG;
                if (j == 2) return CLEAR;
                return 0;
            } else if (i < numRows) {
                return 1 + i * 3 + j;
            }
            return 0;
        }

        @Override
        public int flags(int i, int j) {
            return 0;
        }
    }
}