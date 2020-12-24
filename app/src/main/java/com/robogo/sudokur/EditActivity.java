package com.robogo.sudokur;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class EditActivity extends AppCompatActivity {
    private SudokuView sudokuView;
    private SudokuBoard sudokuBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        sudokuBoard = new SudokuBoard();
        sudokuBoard.init(new int[Sudoku.Size][Sudoku.Size]);
        sudokuView = findViewById(R.id.sudoku_view);
        sudokuView.setSudokuBoard(sudokuBoard);
    }

    public void onCancel(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void onOk(View view) {
        Intent intent = getIntent();
        intent.putExtra(SudokuBoard.NAME, sudokuBoard);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}