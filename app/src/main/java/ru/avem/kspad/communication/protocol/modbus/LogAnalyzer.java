package ru.avem.kspad.communication.protocol.modbus;

import android.util.Log;

public class LogAnalyzer {
    private final String TAG = LogAnalyzer.class.getSimpleName();

    private final String name;

    private int all;
    private int correct;

    public LogAnalyzer(String name) {
        this.name = name;
    }

    public void addWrite() {
        all++;
    }

    public void addSuccess() {
        correct++;
        float failure = all - correct;
        Log.d(TAG, String.format(
                "[%s] All: %d, Correct: %d, Failure: %.0f, Failure Percent: %.4f\n",
                name, all, correct, failure, failure / all));
    }
}
