package ru.avem.kspad.communication.devices.ikas;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceController;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class IKASController implements DeviceController {
    private static final byte MODBUS_ADDRESS = 0x25;
    private static final short STATUS_REGISTER = 0;
    private static final short MEASURABLE_REGISTER = 1;

    private static final int NUM_OF_WORDS_IN_REGISTER = 1;
    private static final short NUM_OF_REGISTERS = 2 * NUM_OF_WORDS_IN_REGISTER;

    private IKASModel mModel;
    private ModbusController mModbusController;
    private byte mAttempt = NUMBER_OF_ATTEMPTS;
    private boolean mNeedToReed;

    public IKASController(Observer observer, ModbusController controller) {
        mModel = new IKASModel(observer);
        mModbusController = controller;
    }

    @Override
    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (thereAreAttempts()) {
            mAttempt--;
            ModbusController.RequestStatus status = mModbusController.readInputRegisters(
                    MODBUS_ADDRESS, STATUS_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                mModel.setResponding(true);
                resetAttempts();
                mModel.setReady(inputBuffer.getFloat());
                mModel.setMeasurable(inputBuffer.getFloat());
            } else {
                read(args);
            }
        } else {
            mModel.setResponding(false);
        }
    }

    @Override
    public void write(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (thereAreAttempts()) {
            mAttempt--;
            ModbusController.RequestStatus status = mModbusController.writeSingleHoldingRegister(MODBUS_ADDRESS,
                    (short) args[0], intToByteArray((int) args[1]), inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                mModel.setResponding(true);
                resetAttempts();
            } else {
                write(args);
            }
        } else {
            mModel.setResponding(false);
        }
    }

    private byte[] intToByteArray(int i) {
        ByteBuffer convertBuffer = ByteBuffer.allocate(4);
        convertBuffer.clear();
        return convertBuffer.putInt(i).array();
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