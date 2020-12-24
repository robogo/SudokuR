package com.robogo.sudokur;

import java.io.IOException;
import java.io.Serializable;

public class SudokuBoard extends Board implements Serializable {
    public static final String NAME = "com.robogo.sudokur.Board";
    private int[][] board;

    public void init(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] > 0) {
                    board[i][j] |= READONLY_MASK;
                }
            }
        }
        this.board = board;
    }

    public void init(SudokuBoard board) {
        init(board.board);
    }

    public boolean initialized() {
        return board != null;
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

    private void set(int i, int j, int bitMask, int value) {
        board[i][j] &= ~bitMask;
        board[i][j] |= value;
    }

    private int get(int i, int j, int bitMask) {
        return board[i][j] & bitMask;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        if (board == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(board.length);
            out.writeInt(board[0].length);
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    out.writeInt(board[i][j]);
                }
            }
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException {
        int row = in.readInt();
        if (row == -1) return;
        int col = in.readInt();
        board = new int[row][];
        for (int i = 0; i < row; i++) {
            board[i] = new int[col];
            for (int j = 0; j < col; j++)
                board[i][j] = in.readInt();
        }
    }
}