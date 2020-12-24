package com.robogo.sudokur;

class Action {
    public static final int PUT = 0;
    public static final int CLEAR = 1;
    public static final int LOCK = 2;
    public static final int UNLOCK = 3;
    public static final int FLAG = 4;
    public static final int UNFLAG = 5;

    int code;
    int row;
    int col;
    int value;

    private Action(int code, int row, int col, int value) {
        this.code = code;
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public static Action put(int row, int col, int value) {
        return new Action(PUT, row, col, value);
    }

    public static Action clear(int row, int col, int value) {
        return new Action(CLEAR, row, col, value);
    }

    public static Action lock(int row, int col) {
        return new Action(LOCK, row, col, 0);
    }

    public static Action unlock(int row, int col) {
        return new Action(UNLOCK, row, col, 0);
    }

    public static Action flag(int row, int col) {
        return new Action(FLAG, row, col, 0);
    }

    public static Action unflag(int row, int col) {
        return new Action(UNFLAG, row, col, 0);
    }
}
