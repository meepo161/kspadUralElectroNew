package ru.avem.kspad.communication.devices;

public interface Device {
    int INPUT_BUFFER_SIZE = 256;

    int BECKHOFF_CONTROL_ID = 1;
    int M40_ID = 2;
    int FR_A800_OBJECT_ID = 0x0B;
    int FR_A800_GENERATOR_ID = 0X0C;
    int PM130_ID = 0x1E;
    int VOLTMETER_ID = 8;
    int TRM201_ID = 0x24;
    int IKAS_ID = 0x25;
    int VEHA_T_ID = 3;
    int MEGGER_ID = 9;

    void read(Object... args);

    boolean isThereAreReadAttempts();

    void write(Object... args);

    boolean isThereAreWriteAttempts();

    boolean isNeedToRead();

    void resetAndStart();

    void resetAttemptsToOneAndStart();

    void resetReadAndWriteAttempts();

    void resetAttemptsToOne();

    void finish();
}
