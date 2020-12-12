package com.robogo.sudokur;

import android.app.Dialog;
import android.view.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private SudokuView sudokuView;
    private SudokuBoard sudokuBoard;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        level = 2;
        sudokuBoard = new SudokuBoard();

        sudokuView = findViewById(R.id.sudoku_view);
        sudokuView.setSudokuBoard(sudokuBoard);
    }

    public void onNewGame(View view) {
        sudokuBoard.init(Sudoku.generate(level), false);
        sudokuView.invalidate();
    }

    public void onOption(View view) {
    }
}