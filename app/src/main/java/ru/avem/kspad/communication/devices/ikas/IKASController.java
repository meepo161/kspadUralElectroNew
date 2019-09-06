package ru.avem.kspad.communication.devices.ikas;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceController;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class IKASController implements DeviceController {
    private static final byte MODBUS_ADDRESS = 0x25;
    private static final short STATUS_REGISTER = 0;
    public static final short MEASURABLE_TYPE_REGISTER = 0x65;
    public static final short START_MEASURABLE_REGISTER = 0x64;
    public static final short TYPE_OF_RANGE_R_REGISTER = 0x67;
    public static final short RANGE_R_REGISTER = 0x68;
    public static final int MEASURABLE_TYPE_AB = 0x46;
    public static final int MEASURABLE_TYPE_BC = 0x44;
    public static final int MEASURABLE_TYPE_AC = 0x45;
    public static final int RANGE_TYPE_LESS_8 = 0x01;
    public static final int RANGE_TYPE_MORE_8_LESS_200 = 0x02;
    public static final int RANGE_TYPE_MORE_200 = 0x03;

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
            byte[] value = new byte[1];
            if (args[1] instanceof Integer) {
                value = intToByteArray((int) args[1]);
            } else if (args[1] instanceof Float) {
                value = floatToByteArray((float) args[1]);
            }
            ModbusController.RequestStatus status = mModbusController.writeSingleHoldingRegister(MODBUS_ADDRESS,
                    (short) args[0], value, inputBuffer);
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

    private byte[] floatToByteArray(float f) {
        ByteBuffer convertBuffer = ByteBuffer.allocate(4);
        convertBuffer.clear();
        return convertBuffer.putFloat(f).array();
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

    public static int getRangeType(float supposedValue) {
        int rangeType = 1;
        if (supposedValue < 8) {
            rangeType = RANGE_TYPE_LESS_8;
        } else if (supposedValue > 8 && supposedValue < 200) {
            rangeType = RANGE_TYPE_MORE_8_LESS_200;
        } else if (supposedValue > 200) {
            rangeType = RANGE_TYPE_MORE_200;
        }
        return rangeType;
    }
}