package ru.avem.kspad.communication.devices.m40;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.BaseDevice;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class M40Controller extends BaseDevice {
    private static final short TORQUE_REGISTER = 0;
    public static final short AVERAGING_REGISTER = 1;

    private static final int CONVERT_BUFFER_SIZE = 4;
    private static final int NUM_OF_WORDS_IN_REGISTER = 2;
    private static final short NUM_OF_REGISTERS = 1 * NUM_OF_WORDS_IN_REGISTER;


    public M40Controller(int modbusAddress, Observer observer, ModbusController controller) {
        address = (byte) modbusAddress;
        model = new M40Model(observer, address);
        modbusController = controller;
    }

    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (isThereAreReadAttempts()) {
            if (readAttempt >= 0) readAttempt--;
            ModbusController.RequestStatus status = modbusController.readInputRegisters(
                    address, TORQUE_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                model.setReadResponding(true);
                resetReadAttempts();
                ((M40Model) model).setTorque(convertToMidLittleEndian(inputBuffer.getInt()) * 1.025f);
//                mModel.setRotationFrequency(convertToMidLittleEndian(inputBuffer.getInt()));
            } else {
                read(args);
            }
        } else {
            if (readAttemptOfAttempt >= 0) readAttemptOfAttempt--;
            if (readAttemptOfAttempt <= 0) {
                model.setReadResponding(false);
                ((M40Model) model).setTorque(0); //TODO ???
            } else {
                resetReadAttempts();
            }
        }
    }

    @Override
    public void write(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (isThereAreWriteAttempts()) {
            if (writeAttempt >= 0) writeAttempt--;
            ModbusController.RequestStatus status = modbusController.writeSingleHoldingRegister(address,
                    (short) args[0], shortToByteArray((short) args[1]), inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                model.setWriteResponding(true);
                resetWriteAttempts();
            } else {
                write(args);
            }
        } else {
            if (writeAttemptOfAttempt >= 0) writeAttemptOfAttempt--;
            if (writeAttemptOfAttempt <= 0) {
                model.setWriteResponding(false);
            } else {
                resetWriteAttempts();
            }
        }
    }

    private byte[] shortToByteArray(short s) {
        ByteBuffer convertBuffer = ByteBuffer.allocate(2);
        convertBuffer.clear();
        return convertBuffer.putShort(s).array();
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
}