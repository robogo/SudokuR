package com.robogo.sudokur;

import android.app.Dialog;
import android.view.*;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private SudokuView sudokuView;
    private SudokuBoard sudokuBoard;
    private ImageButton buttonLevel;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        level = 1;
        sudokuBoard = new SudokuBoard();

        buttonLevel = findViewById(R.id.button_level);
        sudokuView = findViewById(R.id.sudoku_view);
        sudokuView.setSudokuBoard(sudokuBoard);
        buttonLevel.setImageResource(getLevelId(level));
    }

    public void onLevel(View view) {
        level = (level + 1) % 4;
        buttonLevel.setImageResource(getLevelId(level));
    }

    public void onNewGame(View view) {
        sudokuBoard.init(Sudoku.generate(level), false);
        sudokuView.invalidate();
    }

    public void onSolution(View view) {
        sudokuBoard.solve();
        sudokuView.invalidate();
    }

    public void onOption(View view) {
    }

    int getLevelId(int level) {
        switch (level) {
            case 0: return R.drawable.ic_level1;
            case 1: return R.drawable.ic_level2;
            case 2: return R.drawable.ic_level3;
            case 3: return R.drawable.ic_level4;
            default: return -1;
        }
    }
}