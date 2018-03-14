package ru.avem.kspad.communication.protocol.modbus;

import java.util.Locale;

import ru.avem.kspad.utils.Logger;

class LogAnalyzer {
    private static int sWrite;
    private static int sSuccess;

    static void addWrite() {
        sWrite++;
    }

    static void addSuccess() {
        sSuccess++;
        Logger.withTag("LogAnalyzer").log(String.format(Locale.getDefault(),
                "Записано: %d, Удач: %d, Разница: %d, Процент: %.4f",
                sWrite, sSuccess, sWrite - sSuccess, (sWrite - sSuccess) / (float) sWrite));
    }
}
