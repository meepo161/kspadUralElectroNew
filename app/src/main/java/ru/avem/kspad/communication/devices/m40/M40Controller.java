package ru.avem.kspad.communication.devices.m40;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceController;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class M40Controller implements DeviceController {
    private static final byte MODBUS_ADDRESS = 0x2;
    private static final short TORQUE_REGISTER = 0;
    public static final short AVERAGING_REGISTER = 1;

    private static final int CONVERT_BUFFER_SIZE = 4;
    private static final int NUM_OF_WORDS_IN_REGISTER = 2;
    private static final short NUM_OF_REGISTERS = 1 * NUM_OF_WORDS_IN_REGISTER;

    private M40Model mModel;
    private ModbusController mModbusController;
    private byte mAttempt = NUMBER_OF_ATTEMPTS;
    private boolean mNeedToReed;

    public M40Controller(Observer observer, ModbusController modbusController) {
        mModel = new M40Model(observer);
        mModbusController = modbusController;
    }

    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (thereAreAttempts()) {
            mAttempt--;
            ModbusController.RequestStatus status = mModbusController.readInputRegisters(
                    MODBUS_ADDRESS, TORQUE_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                mModel.setResponding(true);
                resetAttempts();
                mModel.setTorque(convertToMidLittleEndian(inputBuffer.getInt()) * 1.025f);
//                mModel.setRotationFrequency(convertToMidLittleEndian(inputBuffer.getInt()));
            } else {
                read(args);
            }
        } else {
            mModel.setResponding(false);
            mModel.setTorque(0);
//            mModel.setRotationFrequency(0);
        }
    }

    @Override
    public void write(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (thereAreAttempts()) {
            mAttempt--;
            ModbusController.RequestStatus status = mModbusController.writeSingleHoldingRegister(MODBUS_ADDRESS,
                    (short) args[0], shortToByteArray((short) args[1]), inputBuffer);
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

    private byte[] shortToByteArray(short s) {
        ByteBuffer convertBuffer = ByteBuffer.allocate(2);
        convertBuffer.clear();
        return convertBuffer.putShort(s).array();
    }

    @Override
    public void resetAttempts() {
        mAttempt = NUMBER_OF_ATTEMPTS;
    }

    @Override
    public boolean thereAreAttempts() {
        return mAttempt > 0;
    }

    private float convertToMidLittleEndian(int i) {
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
        return convertBuffer.getFloat();
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