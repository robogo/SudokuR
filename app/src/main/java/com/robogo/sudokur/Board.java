package com.robogo.sudokur;

public abstract class Board {
    public abstract int size();

    public abstract int value(int i, int j);

    public abstract boolean readonly(int i, int j);
}
