package com.robogo.sudokur;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private static final int LAUNCH_EDIT_ACTIVITY = 1;
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
        sudokuBoard.init(Sudoku.generate(level));
        sudokuView.invalidate();
    }

    public void onManualGame(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        startActivityForResult(intent, LAUNCH_EDIT_ACTIVITY);
    }

    public void onUndo(View view) {
        if (sudokuBoard.initialized()) {
            sudokuBoard.undo();
            sudokuView.invalidate();
        }
    }

    public void onUndoN(View view) {
        if (sudokuBoard.initialized()) {
            sudokuBoard.undoN();
            sudokuView.invalidate();
        }
    }

    public void onSolution(View view) {
        if (sudokuBoard.initialized()) {
            sudokuBoard.solve();
            sudokuView.invalidate();
        }
    }

    public void onOption(View view) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_EDIT_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                SudokuBoard b = (SudokuBoard)data.getSerializableExtra(SudokuBoard.NAME);
                sudokuBoard.init(b);
            }
        }
    }

    private int getLevelId(int level) {
        switch (level) {
            case 0: return R.drawable.ic_level1;
            case 1: return R.drawable.ic_level2;
            case 2: return R.drawable.ic_level3;
            case 3: return R.drawable.ic_level4;
            default: return -1;
        }
    }
}