package ru.avem.kspad.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.List;
import java.util.Locale;

import ru.avem.kspad.R;

public class Utils {
    public static final Locale RU_LOCALE = new Locale("ru");

    private static final float NUM_OF_POINTS = 3f;

    private Utils() {
        throw new AssertionError();
    }

    public static void setSpinnerAdapter(Context context, Spinner spinner, List<?> list) {
        ArrayAdapter<?> arrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_layout,
                list);
        spinner.setAdapter(arrayAdapter);
    }

    public static void setListViewAdapterFromResources(Context context, ListView listView,
                                                       int resourceId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, resourceId,
                android.R.layout.simple_list_item_multiple_choice);
        listView.setAdapter(adapter);
    }

    public static void setSpinnerAdapterFromResources(Context context, Spinner spinner,
                                                      int resourceId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, resourceId,
                R.layout.spinner_layout);
        spinner.setAdapter(adapter);
    }

    public static String formatRealNumber(double num) {
        num = Math.abs(num);
        String format = "%.0f";
        if (num == 0) {
            format = "%.0f";
        } else if (num < 0.1f) {
            format = "%.5f";
        } else if (num < 1f) {
            format = "%.4f";
        } else if (num < 10f) {
            format = "%.3f";
        } else if (num < 100f) {
            format = "%.2f";
        } else if (num < 1000f) {
            format = "%.1f";
        } else if (num < 10000f) {
            format = "%.0f";
        }
        return String.format(RU_LOCALE, format, num);
    }

    public static int getSyncV(int F, int N) {
        for (int p = 2; p < 8; p++) {
            int sync = F * 60 / p;
            if (N > sync) {
                return F * 60 / (p - 1);
            }
        }
        return 0;
    }

    public static void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ignored) {
        }
    }

    public static float setNextValueAndReturnAverage(List<Float> list, float value) {
        if (list.size() < NUM_OF_POINTS) {
            list.add(value);
            return -1f;
        } else {
            float sum = 0;
            for (Float f : list) {
                sum += f;
            }
            sum /= NUM_OF_POINTS;
            list.clear();
            return sum;
        }
    }
}