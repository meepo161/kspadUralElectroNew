package ru.avem.kspad.communication.devices.pm130_ia;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceController;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class PM130ControllerIA implements DeviceController {
    private static final byte MODBUS_ADDRESS = 0x1E;
    private static final short I1_REGISTER = 13318;

    private static final int CONVERT_BUFFER_SIZE = 4;
    private static final float I_DIVIDER = 100.f;
    private static final int I_MULTIPLIER = 1000;
    private static final int NUM_OF_WORDS_IN_REGISTER = 2;
    private static final short NUM_OF_REGISTERS = 1 * NUM_OF_WORDS_IN_REGISTER;

    private PM130ModelIA mModel;
    private ModbusController mModbusController;
    private byte mAttempt = NUMBER_OF_ATTEMPTS;
    private boolean mNeedToReed;

    public PM130ControllerIA(Observer observer, ModbusController modbusController) {
        mModel = new PM130ModelIA(observer);
        mModbusController = modbusController;
    }

    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (thereAreAttempts()) {
            mAttempt--;
            ModbusController.RequestStatus status = mModbusController.readInputRegisters(
                    MODBUS_ADDRESS, I1_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                resetAttempts();
                mModel.setResponding(true);
                mModel.setI1(convertUINTtoINT(inputBuffer.getInt()) * I_MULTIPLIER / I_DIVIDER / 1000);
            } else {
                read(args);
            }
        } else {
            mModel.setResponding(false);
            mModel.setI1(0);
        }
    }

    @Override
    public void write(Object... args) {
    }

    @Override
    public void resetAttempts() {
        mAttempt = NUMBER_OF_ATTEMPTS;
    }

    @Override
    public boolean thereAreAttempts() {
        return mAttempt > 0;
    }

    private long convertUINTtoINT(int i) {
        ByteBuffer convertBuffer = ByteBuffer.allocate(CONVERT_BUFFER_SIZE);
        convertBuffer.clear();
        convertBuffer.putInt(i);
        convertBuffer.flip();
        short rightSide = convertBuffer.getShort();
        short leftSide = convertBuffer.getShort();
        convertBuffer.clear();
        convertBuffer.putShort(leftSide);
        convertBuffer.putShort(rightSide);
        convertBuffer.flip();
        int preparedInt = convertBuffer.getInt();
        return (long) preparedInt & 0xFFFFFFFFL;
    }

    @Override
    public boolean needToRead() {
        return mNeedToReed;
    }

    @Override
    public void setNeedToRead(boolean needToRead) {
        mNeedToReed = needToRead;
    }
}