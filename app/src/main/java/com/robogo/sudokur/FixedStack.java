package com.robogo.sudokur;

import androidx.core.util.Predicate;

class FixedStack<T> {
    private T[] array;
    private int top;
    private int count;

    public FixedStack(int size) {
        array = (T[])new Object[size];
    }

    public int size() {
        return count;
    }

    public void push(T t) {
        array[top] = t;
        top = forward(top);
        if (count < array.length)
            count++;
    }

    public T pop() {
        throwIfEmpty();
        count--;
        top = back(top);
        return array[top];
    }

    public T peek() {
        throwIfEmpty();
        return array[back(top)];
    }

    public boolean find(Predicate<T> p) {
        int remaining = count;
        int i = top;
        while (remaining-- > 0) {
            i = back(i);
            if (p.test(array[i]))
                return true;
        }
        return false;
    }

    private void throwIfEmpty() {
        if (count == 0) {
            throw new IllegalStateException("No item to pop");
        }
    }

    private int back(int i) {
        i--;
        if (i < 0)
            i = array.length - 1;
        return i;
    }

    private int forward(int i) {
        i++;
        if (i == array.length)
            i = 0;
        return i;
    }
}
