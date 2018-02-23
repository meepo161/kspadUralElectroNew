package ru.avem.kspad.communication.devices;

public interface DeviceController {
    int INPUT_BUFFER_SIZE = 256;
    int NUMBER_OF_ATTEMPTS = 14;

    int BECKHOFF_CONTROL_ID = 0;
    int M40_ID = 1;
    int FR_A800_OBJECT_ID = 2;
    int FR_A800_GENERATOR_ID = 3;
    int PM130_ID = 4;
    int VOLTMETER_ID = 5;
    int TRM201_ID = 6;
    int IKAS_ID = 7;
    int VEHA_T_ID = 8;

    void read(Object... args);

    void write(Object... args);

    void resetAttempts();

    boolean thereAreAttempts();

    boolean needToRead();

    void setNeedToRead(boolean needToRead);
}