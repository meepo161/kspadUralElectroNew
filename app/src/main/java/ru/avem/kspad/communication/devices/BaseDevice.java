package ru.avem.kspad.communication.devices;


import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public abstract class BaseDevice implements Device {
    private final byte NUMBER_OF_READ_ATTEMPTS = 5;
    private final byte NUMBER_OF_WRITE_ATTEMPTS = 5;
    private final byte NUMBER_OF_READ_ATTEMPTS_OF_ATTEMPTS = 5;
    private final byte NUMBER_OF_WRITE_ATTEMPTS_OF_ATTEMPTS = 5;

    protected boolean isNeedToRead;

    protected byte readAttempt = NUMBER_OF_READ_ATTEMPTS;
    protected byte readAttemptOfAttempt = NUMBER_OF_READ_ATTEMPTS_OF_ATTEMPTS;
    protected byte writeAttempt = NUMBER_OF_WRITE_ATTEMPTS;
    protected byte writeAttemptOfAttempt = NUMBER_OF_WRITE_ATTEMPTS_OF_ATTEMPTS;

    protected byte address;
    protected DeviceModel model;
    protected ModbusController modbusController;

    @Override
    public boolean isThereAreReadAttempts() {
        return readAttempt > 0;
    }

    @Override
    public void write(Object... args) {

    }

    @Override
    public boolean isThereAreWriteAttempts() {
        return writeAttempt > 0;
    }

    @Override
    public boolean isNeedToRead() {
        return isNeedToRead;
    }

    private void setNeedToRead(boolean isNeedToRead) {
        if (isNeedToRead) {
            model.resetResponding();
        }
        this.isNeedToRead = isNeedToRead;
    }

    @Override
    public void resetAndStart() {
        resetReadAndWriteAttempts();
        setNeedToRead(true);
    }

    @Override
    public void resetAttemptsToOneAndStart() {
        resetAttemptsToOne();
        setNeedToRead(true);
    }

    @Override
    public void resetReadAndWriteAttempts() {
        resetReadAttempts();
        resetReadAttemptsOfAttempts();
        resetWriteAttempts();
        resetWriteAttemptsOfAttempts();
    }

    public void resetReadAttempts() {
        readAttempt = NUMBER_OF_READ_ATTEMPTS;
    }

    public void resetReadAttemptsOfAttempts() {
        readAttemptOfAttempt = NUMBER_OF_READ_ATTEMPTS_OF_ATTEMPTS;
    }

    public void resetWriteAttempts() {
        writeAttempt = NUMBER_OF_WRITE_ATTEMPTS;
    }

    public void resetWriteAttemptsOfAttempts() {
        writeAttemptOfAttempt = NUMBER_OF_WRITE_ATTEMPTS_OF_ATTEMPTS;
    }

    @Override
    public void resetAttemptsToOne() {
        readAttempt = 1;
        readAttemptOfAttempt = 0;
        writeAttempt = 1;
        writeAttemptOfAttempt = 0;
    }

    @Override
    public void finish() {
        setNeedToRead(false);
    }
}
