package ru.avem.kspad.communication.devices.veha_t;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceController;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class VEHATController implements DeviceController {
    private static final byte MODBUS_ADDRESS = 0x03;
    private static final short ROTATION_FREQUENCY_REGISTER = 1; // это конфигурация TH01 0x29

    private static final int NUM_OF_WORDS_IN_REGISTER = 2;
    private static final short NUM_OF_REGISTERS = 1 * NUM_OF_WORDS_IN_REGISTER;

    private VEHATModel mModel;
    private ModbusController mModbusController;
    private byte mAttempt = NUMBER_OF_ATTEMPTS;
    private boolean mNeedToReed;

    public VEHATController(Observer observer, ModbusController controller) {
        mModel = new VEHATModel(observer);
        mModbusController = controller;
    }

    @Override
    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (thereAreAttempts()) {
            mAttempt--;
            ModbusController.RequestStatus status = mModbusController.readInputRegisters(
//            ModbusController.RequestStatus status = mModbusController.readMultipleHoldingRegisters( это конфигурация TH01
                    MODBUS_ADDRESS, ROTATION_FREQUENCY_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                mModel.setResponding(true);
                resetAttempts();
                mModel.setRotationFrequency(convertToMidLittleEndian(inputBuffer.getFloat()));
//                mModel.setRotationFrequency(inputBuffer.getInt()); это конфигурация TH01
            } else {
                read(args);
            }
        } else {
            mModel.setResponding(false);
        }
    }

    private float convertToMidLittleEndian(float f) {
        ByteBuffer convertBuffer = ByteBuffer.allocate(4);
        convertBuffer.clear();
        convertBuffer.putFloat(f);
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