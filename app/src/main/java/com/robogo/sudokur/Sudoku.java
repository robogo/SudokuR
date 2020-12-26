package com.robogo.sudokur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Sudoku {
    public static final int Size = 9;

    public static int[][] generate(int level) {
        // generate a full board
        int[][] board = new int[Size][];
        for (int i = 0; i < Size; i++) {
            for (int j = 0; j < Size; j++) {
                board[i] = new int[Size];
            }
        }
        tryGenerate(board);

        // keep removing cells based on difficulty level
        level = Math.max(0, Math.min(3, level));
        int minToKeep = 18 + (3 - level) * 3;
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < Size * Size; i++)
            list.add(i);
        Collections.shuffle(list);
        int possibility = 1;
        while (list.size() > minToKeep && possibility < level + 2) {
            boolean removed = false;
            for (int i = 0; i < list.size(); i++) {
                int x = list.get(i) / Size;
                int y = list.get(i) % Size;
                if (findPossible(board, x, y, null) <= possibility) {
                    board[x][y] = 0;
                    list.remove(i);
                    removed = true;
                    break;
                }
            }
            if (!removed) {
                possibility++;
            }
        }

        return board;
    }

    public static boolean solve(int[][] board) {
        if (board.length != board[0].length && board.length != Size)
            return false;
        return trySolve(board);
    }

    public static long check(int[][] board, int row, int col, int val) {
        int rowConflict = 0;
        int colConflict = 0;
        int squareConflict = 0;
        for (int i = 0; i < board.length; i++) {
            int value = board[i][col] & 0xFF;
            if (i != row && value == val)
                rowConflict = (i << 8) + col;
        }
        for (int i = 0; i < board[row].length; i++) {
            int value = board[row][i] & 0xFF;
            if (i != col && value == val)
                colConflict = (row << 8) + i;
        }
        int rr = row / 3 * 3;
        int cc = col / 3 * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (rr + i != row && cc + j != col) {
                    int bv = board[rr + i][cc + j] & 0xFF;
                    if (bv == val)
                        squareConflict = (rr + i) << 8 + (cc + j);
                }
            }
        }
        return (squareConflict << 32) + (colConflict << 16) + rowConflict;
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

        ArrayList<Integer> list = new ArrayList<>();
        int count = findPossible(board, fr, fc, list);
        if (count == 0) {
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
                int value = board[i][j] & 0xFF;
                if (value == 0) {
                    hasZero = true;
                    ArrayList<Integer> list = new ArrayList<>();
                    int count = findPossible(board, i, j, list);
                    if (count == 0)
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
            int old = board[fr][fc];
            board[fr][fc] &= ~0xFF;
            board[fr][fc] |= first.get(i);
            boolean ret = trySolve(board);
            if (ret)
                return ret;
            board[fr][fc] = old;
        }

        return false;
    }

    static void print(int[][] board) {
        for (int i = 0; i < board.length; i++)
            System.out.println(Arrays.toString(board[i]));
        System.out.println();
    }

    static int findPossible(int[][] board, int row, int col, ArrayList<Integer> list) {
        int bits = 0xFFFF;
        int count = 0;
        for (int i = 0; i < board[row].length; i++) {
            int value = board[row][i] & 0xFF;
            if (i != col && value > 0)
                bits &= ~(1 << value);
        }
        for (int i = 0; i < board.length; i++) {
            int value = board[i][col] & 0xFF;
            if (i != row && value > 0)
                bits &= ~(1 << value);
        }
        int rr = row / 3 * 3;
        int cc = col / 3 * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (rr + i != row && cc + j != col) {
                    int bv = board[rr + i][cc + j] & 0xFF;
                    if (bv > 0)
                        bits &= ~(1 << bv);
                }
            }
        }
        for (int i = 1; i <= board.length; i++) {
            if ((bits & (1 << i)) > 0) {
                count++;
                if (list != null)
                    list.add(i);
            }
        }
        return count;
    }
}
