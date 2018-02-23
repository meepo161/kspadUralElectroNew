package ru.avem.kspad.communication.devices.trm201;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceController;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class TRM201Controller implements DeviceController {
    private static final byte MODBUS_ADDRESS = 0x24;
    private static final short T_AMBIENT_REGISTER = 1;

    private static final int NUM_OF_WORDS_IN_REGISTER = 1;
    private static final short NUM_OF_REGISTERS = 2 * NUM_OF_WORDS_IN_REGISTER;

    private TRM201Model mModel;
    private ModbusController mModbusController;
    private byte mAttempt = NUMBER_OF_ATTEMPTS;
    private boolean mNeedToReed;

    public TRM201Controller(Observer observer, ModbusController controller) {
        mModel = new TRM201Model(observer);
        mModbusController = controller;
    }

    @Override
    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (thereAreAttempts()) {
            mAttempt--;
            ModbusController.RequestStatus status = mModbusController.readMultipleHoldingRegisters(
                    MODBUS_ADDRESS, T_AMBIENT_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                mModel.setResponding(true);
                resetAttempts();
                mModel.setTAmbient(inputBuffer.getShort() / 10f);
                mModel.setTEngine(inputBuffer.getShort() / 10f);
            } else {
                read(args);
            }
        } else {
            mModel.setResponding(false);
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

    @Override
    public boolean needToRead() {
        return mNeedToReed;
    }

    @Override
    public void setNeedToRead(boolean needToRead) {
        mNeedToReed = needToRead;
    }
}