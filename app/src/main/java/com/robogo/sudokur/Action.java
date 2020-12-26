package com.robogo.sudokur;

class Action {
    public static final int PUT = 0;
    public static final int CLEAR = 1;
    public static final int LOCK = 2;
    public static final int FLAG = 3;

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

    public static Action lock(int row, int col, int value) {
        return new Action(LOCK, row, col, value);
    }

    public static Action flag(int row, int col, int value) {
        return new Action(FLAG, row, col, value);
    }
}
