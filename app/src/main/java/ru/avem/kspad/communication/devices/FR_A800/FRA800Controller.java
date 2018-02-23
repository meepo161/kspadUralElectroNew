package ru.avem.kspad.communication.devices.FR_A800;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceController;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class FRA800Controller implements DeviceController {
    public static final short CONTROL_REGISTER = 8;
    public static final short CURRENT_FREQUENCY_REGISTER = 14;
    public static final short MAX_FREQUENCY_REGISTER = 1002;
    public static final short MAX_VOLTAGE_REGISTER = 1018;

    private static final int NUM_OF_WORDS_IN_REGISTER = 1;
    private static final short NUM_OF_REGISTERS = 1 * NUM_OF_WORDS_IN_REGISTER;

    private FRA800Model mModel;
    private byte mModbusAddress;
    private ModbusController mModbusProtocol;
    private byte mAttempt = NUMBER_OF_ATTEMPTS;
    private boolean mNeedToReed;

    public FRA800Controller(int modbusAddress, Observer observer, ModbusController controller, int id) {
        mModbusAddress = (byte) modbusAddress;
        mModel = new FRA800Model(observer, id);
        mModbusProtocol = controller;
    }

    @Override
    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (thereAreAttempts()) {
            mAttempt--;
            ModbusController.RequestStatus status = mModbusProtocol.readMultipleHoldingRegisters(
                    mModbusAddress, CONTROL_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                mModel.setResponding(true);
                mModel.setControlState(inputBuffer.getShort());
                resetAttempts();
            } else {
                read(args);
            }
        }
    }

    @Override
    public void write(Object... args) {
        short register = (short) args[0];
        int numOfRegisters = (int) args[1];
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        ByteBuffer dataBuffer = ByteBuffer.allocate(2 * numOfRegisters);
        for (int i = 2; i < numOfRegisters + 2; i++) {
            dataBuffer.putShort((short) ((int) args[i]));
        }
        dataBuffer.flip();

        if (thereAreAttempts()) {
            mAttempt--;
            ModbusController.RequestStatus status = mModbusProtocol.writeMultipleHoldingRegisters(
                    mModbusAddress, register, (short) numOfRegisters, dataBuffer, inputBuffer);
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