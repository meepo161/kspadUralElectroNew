package ru.avem.kspad.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.avem.kspad.R;

import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment16Activity extends AppCompatActivity {
    private static final String EXPERIMENT_NAME = "Определение уровня шума и вибрации";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment16);
    }

    @Override
    public void onBackPressed() {
        returnValues();
        finish();
    }

    private void returnValues() {
        Intent data = new Intent();
        setResult(RESULT_OK, data);
    }
}
