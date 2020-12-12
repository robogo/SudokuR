package com.robogo.sudokur;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Sudoku {
    public static final int Size = 9;

    public static int[][] generate(int level) {
        int[][] board = new int[Size][];
        for (int i = 0; i < Size; i++) {
            for (int j = 0; j < Size; j++) {
                board[i] = new int[Size];
            }
        }

        tryGenerate(board);

        Random random = new Random();
        level = Math.max(1, Math.min(level, 4));
        int toRemove = Size * Size - (17 + (4 - level) * 2);
        int removed = 0;
        while (removed < toRemove) {
            int i = random.nextInt(Size);
            int j = random.nextInt(Size);
            int val = board[i][j];
            if (val > 0) {
                board[i][j] = 0;
                removed++;
            }
        }

        return board;
    }

    public static boolean solve(int[][] board) {
        if (board.length != board[0].length && board.length != Size)
            return false;
        return trySolve(board);
    }

    static boolean tryGenerate(int[][] board) {
        int row = board.length;
        int col = board[0].length;
        int fr = -1;
        int fc = -1;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (board[i][j] == 0) {
                    fr = i;
                    fc = j;
                    break;
                }
            }
        }

        if (fr < 0)
            return true;

        ArrayList<Integer> list = findPossible(board, fr, fc);
        if (list.size() == 0) {
            return false;
        }

        Collections.shuffle(list);
        for (int i = 0; i < list.size(); i++) {
            board[fr][fc] = list.get(i);
            boolean ret = tryGenerate(board);
            if (ret)
                return true;
            board[fr][fc] = 0;
        }

        return false;
    }

    static boolean trySolve(int[][] board) {
        int row = board.length;
        int col = board[0].length;
        boolean hasZero = false;
        ArrayList<Integer> first = null;
        int fr = -1;
        int fc = -1;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j  < col; j++) {
                if (board[i][j] == 0) {
                    hasZero = true;
                    ArrayList<Integer> list = findPossible(board, i, j);
                    if (list.size() == 0)
                        return false;
                    if (first == null || first.size() > list.size()) {
                        first = list;
                        fr = i;
                        fc = j;
                    }
                }
            }
        }

        if (first == null)
            return !hasZero;

        for (int i = 0; i < first.size(); i++) {
            board[fr][fc] = first.get(i);
            boolean ret = trySolve(board);
            if (ret)
                return ret;
            board[fr][fc] = 0;
        }

        return false;
    }

    static void print(int[][] board) {
        for (int i = 0; i < board.length; i++)
            System.out.println(Arrays.toString(board[i]));
        System.out.println();
    }

    static ArrayList<Integer> findPossible(int[][] board, int row, int col) {
        ArrayList<Integer> list = new ArrayList<>();
        int bits = 0xFFFF;
        for (int i = 0; i < board[row].length; i++) {
            if (board[row][i] > 0)
                bits &= ~(1 << board[row][i]);
        }
        for (int i = 0; i < board.length; i++) {
            if (board[i][col] > 0)
                bits &= ~(1 << board[i][col]);
        }
        int rr = row / 3 * 3;
        int cc = col / 3 * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int bv = board[rr + i][cc + j];
                if (bv > 0)
                    bits &= ~(1 << bv);
            }
        }
        for (int i = 1; i <= board.length; i++) {
            if ((bits & (1 << i)) > 0)
                list.add(i);
        }
        return list;
    }
}
