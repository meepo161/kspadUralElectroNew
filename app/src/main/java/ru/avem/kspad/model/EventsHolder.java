package ru.avem.kspad.model;

import java.util.ArrayList;
import java.util.List;

public class EventsHolder {
    private static List<String> sEventsLog = new ArrayList<>();

    public static List<String> getEventLogs() {
        return sEventsLog;
    }

    public static void addLine(String line) {
        sEventsLog.add(line);
    }
}
