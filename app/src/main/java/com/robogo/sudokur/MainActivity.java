package com.robogo.sudokur;

import android.app.Dialog;
import android.view.*;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private SudokuView sudokuView;
    private SudokuBoard sudokuBoard;
    private Button buttonLevel;
    private int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        level = 0;
        sudokuBoard = new SudokuBoard();

        buttonLevel = findViewById(R.id.button_level);
        sudokuView = findViewById(R.id.sudoku_view);
        sudokuView.setSudokuBoard(sudokuBoard);
        buttonLevel.setText(getLevelId(level));
    }

    public void onLevel(View view) {
        level = (level + 1) % 4;
        buttonLevel.setText(getLevelId(level));
    }

    public void onNewGame(View view) {
        sudokuBoard.init(Sudoku.generate(level), false);
        sudokuView.invalidate();
    }

    public void onOption(View view) {
    }

    int getLevelId(int level) {
        switch (level) {
            case 0: return R.string.level_novice;
            case 1: return R.string.level_casual;
            case 2: return R.string.level_skilled;
            case 3: return R.string.level_expert;
            default: return -1;
        }
    }
}