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
        check(i, j, value);
        history.push(Action.put(i, j, old));
    }

    public void clear(int i, int j) {
        int old = board[i][j];
        board[i][j] = 0;
        check(i, j, 0);
        history.push(Action.clear(i, j, old));
    }

    public boolean lock(int i, int j, boolean v) {
        set(i, j, LOCK_MASK, v ? LOCK_MASK : 0);
        history.push(Action.lock(i, j, v ? 1 : 0));
        return v;
    }

    public boolean flag(int i, int j, boolean v) {
        set(i, j, FLAG_MASK, v ? FLAG_MASK : 0);
        history.push(Action.flag(i, j, v ? 1 : 0));
        return v;
    }

    public boolean conflict(int i, int j, boolean v) {
        set(i, j, CONFLICT_MASK, v ? CONFLICT_MASK : 0);
        return v;
    }

    public void undo() {
        if (history.size() > 0) {
            Action a = history.pop();
            switch (a.code) {
                case Action.PUT:
                    set(a.row, a.col, VALUE_MASK, a.value);
                    check(a.row, a.col, a.value);
                    break;
                case Action.CLEAR:
                    board[a.row][a.col] = a.value;
                    check(a.row, a.col, a.value);
                    break;
                case Action.LOCK:
                    set(a.row, a.col, LOCK_MASK, a.value == 0 ? LOCK_MASK : 0);
                    break;
                case Action.FLAG:
                    set(a.row, a.col, FLAG_MASK, a.value == 0 ? FLAGS_MASK : 0);
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

    private void check(int i, int j, int value) {
        boolean conflicting = false;
        for (int r = 0; r < row(); r++) {
            if (r != i)
                conflicting |= conflict(r, j, value == value(r, j));
        }
        for (int c = 0; c < col(); c++) {
            if (c != j)
                conflicting |= conflict(i, c, value == value(i, c));
        }
        for (int r = 0; r < 3; r++) {
            int rr = i / 3 * 3 + r;
            for (int c = 0; c < 3; c++) {
                int cc = j / 3 * 3 + c;
                if (rr != i && cc != j) {
                    conflicting |= conflict(rr, cc, value == value(rr, cc));
                }
            }
        }
        conflict(i, j, conflicting);
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