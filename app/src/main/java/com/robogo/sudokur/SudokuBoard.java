package com.robogo.sudokur;

public class SudokuBoard extends Board {
    private int[][] board;
    private boolean readonly = true;

    public void init(int[][] board, boolean readOnly) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (readOnly || board[i][j] > 0) {
                    board[i][j] |= READONLY_MASK;
                }
            }
        }
        this.board = board;
        this.readonly = readOnly;
    }

    public boolean initialized() {
        return board != null;
    }

    public boolean readonly() {
        return readonly;
    }

    @Override
    public int row() {
        return Sudoku.Size;
    }

    @Override
    public int col() { return Sudoku.Size; }

    @Override
    public int value(int i, int j) {
        return get(i, j, VALUE_MASK);
    }

    @Override
    public int flags(int i, int j) {
        return get(i, j, FLAGS_MASK);
    }

    public void lock(int i, int j) {
        set(i, j, LOCK_MASK, LOCK_MASK);
    }

    public void unlock(int i, int j) {
        set(i, j, LOCK_MASK, 0);
    }

    public void flag(int i, int j) {
        set(i, j, FLAG_MASK, FLAG_MASK);
    }

    public void unflag(int i, int j) {
        set(i, j, FLAG_MASK, 0);
    }

    public void set(int i, int j, int value) {
        set(i, j, VALUE_MASK, value);
    }

    public void clear(int i, int j) {
        board[i][j] = 0;
    }

    public void solve() {
        Sudoku.solve(board);
    }

    void set(int i, int j, int bitMask, int value) {
        board[i][j] &= ~bitMask;
        board[i][j] |= value;
    }

    int get(int i, int j, int bitMask) {
        return board[i][j] & bitMask;
    }
}