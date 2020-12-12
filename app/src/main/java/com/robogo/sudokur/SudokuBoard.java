package com.robogo.sudokur;

public class SudokuBoard extends Board {
    private static final int ValueMask = 0xFF;
    private static final int ReadonlyMask = 0x100;
    private int[][] board;
    private boolean readOnly = true;

    public void init(int[][] board, boolean readOnly) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (readOnly || board[i][j] > 0) {
                    board[i][j] |= ReadonlyMask;
                }
            }
        }
        this.board = board;
        this.readOnly = readOnly;
    }

    public boolean getInitialized() {
        return board != null;
    }

    public boolean getReadonly() {
        return readOnly;
    }

    @Override
    public int size() {
        return Sudoku.Size;
    }

    @Override
    public int value(int i, int j) {
        return board[i][j] & ValueMask;
    }

    @Override
    public boolean readonly(int i, int j) {
        return (board[i][j] & ReadonlyMask) > 0;
    }

    public void setValue(int x, int y, int value) {
        board[x][y] = value;
    }
}