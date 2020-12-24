package com.robogo.sudokur;

import androidx.core.util.Predicate;

import java.io.IOException;
import java.io.Serializable;

public class SudokuBoard extends Board implements Serializable {
    public static final String NAME = "com.robogo.sudokur.Board";
    private static final Predicate<Action> flagPredicate = new Predicate<Action>() {
        @Override
        public boolean test(Action action) {
            return action.code == Action.FLAG;
        }
    };

    private int[][] board;
    private FixedStack<Action> history;

    public void init(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] > 0) {
                    board[i][j] |= READONLY_MASK;
                }
            }
        }
        this.board = board;
        this.history = new FixedStack<>(100);
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

    public void set(int i, int j, int value) {
        int old = value(i, j);
        set(i, j, VALUE_MASK, value);
        history.push(Action.put(i, j, old));
    }

    public void clear(int i, int j) {
        int old = board[i][j];
        board[i][j] = 0;
        history.push(Action.clear(i, j, old));
    }

    public void lock(int i, int j) {
        set(i, j, LOCK_MASK, LOCK_MASK);
        history.push(Action.lock(i, j));
    }

    public void unlock(int i, int j) {
        set(i, j, LOCK_MASK, 0);
        history.push(Action.unlock(i, j));
    }

    public void flag(int i, int j) {
        set(i, j, FLAG_MASK, FLAG_MASK);
        history.push(Action.flag(i, j));
    }

    public void unflag(int i, int j) {
        set(i, j, FLAG_MASK, 0);
        history.push(Action.unflag(i, j));
    }

    public void undo() {
        if (history.size() > 0) {
            Action a = history.pop();
            switch (a.code) {
                case Action.PUT:
                    set(a.row, a.col, VALUE_MASK, a.value);
                    break;
                case Action.CLEAR:
                    board[a.row][a.col] = a.value;
                    break;
                case Action.LOCK:
                    set(a.row, a.col, LOCK_MASK, 0);
                    break;
                case Action.UNLOCK:
                    set(a.row, a.col, LOCK_MASK, LOCK_MASK);
                    break;
                case Action.FLAG:
                    set(a.row, a.col, FLAG_MASK, 0);
                    break;
                case Action.UNFLAG:
                    set(a.row, a.col, FLAG_MASK, FLAGS_MASK);
                    break;
                default:
                    break;
            }
        }
    }

    public void undoN() {
        boolean flagged = history.find(flagPredicate);
        if (!flagged) {
            return;
        }

        while (history.size() > 0) {
            Action a = history.peek();
            if (a.code == Action.FLAG)
                return;
            undo();
        }
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