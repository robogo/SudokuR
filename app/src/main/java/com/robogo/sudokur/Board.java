package com.robogo.sudokur;

public abstract class Board {
    static final int VALUE_MASK = 0xFF;
    static final int FLAGS_MASK = 0xFF00;
    static final int READONLY_MASK = 0x100;
    static final int LOCK_MASK = 0x200;
    static final int FLAG_MASK = 0x400;
    static final int CONFLICT_MASK = 0x800;

    public abstract int row();

    public abstract int col();

    public abstract int value(int i, int j);

    public abstract int flags(int i, int j);

    public boolean readonly(int i, int j) {
        return (flags(i, j) & READONLY_MASK) > 0;
    }

    public boolean locked(int i, int j) {
        return (flags(i, j) & LOCK_MASK) > 0;
    }

    public boolean flagged(int i, int j) {
        return (flags(i, j) & FLAG_MASK) > 0;
    }

    public boolean conflicting(int i, int j) {
        return (flags(i, j) & CONFLICT_MASK) > 0;
    }
}
