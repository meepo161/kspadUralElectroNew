package ru.avem.kspad.utils;

import java.util.ArrayList;
import java.util.List;

public class Log {
    private final static List<String> lines = new ArrayList<>();

    public static void addLine(String line) {
        lines.add(line);
    }

    public static String getLines() {
        StringBuilder sb = new StringBuilder();
        sb.append("Log:\n");
        for (String line : lines) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }
}
