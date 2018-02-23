package ru.avem.kspad.communication.devices.voltmeter;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceController;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class VoltmeterController implements DeviceController {
    private static final byte MODBUS_ADDRESS = 8;
    private static final short U_RMS_REGISTER = 2;

    private static final int NUM_OF_WORDS_IN_REGISTER = 1;
    private static final short NUM_OF_REGISTERS = 1 * NUM_OF_WORDS_IN_REGISTER;

    private VoltmeterModel mModel;
    private ModbusController mModbusController;
    private byte mAttempt = NUMBER_OF_ATTEMPTS;
    private boolean mNeedToReed;

    public VoltmeterController(Observer observer, ModbusController controller) {
        mModel = new VoltmeterModel(observer);
        mModbusController = controller;
    }

    @Override
    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (thereAreAttempts()) {
            mAttempt--;
            ModbusController.RequestStatus status = mModbusController.readInputRegisters(
                    MODBUS_ADDRESS, U_RMS_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                mModel.setResponding(true);
                resetAttempts();
                mModel.setU(inputBuffer.getFloat());
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