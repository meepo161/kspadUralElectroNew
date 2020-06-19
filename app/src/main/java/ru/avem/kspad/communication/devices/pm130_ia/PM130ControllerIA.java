package ru.avem.kspad.communication.devices.pm130_ia;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.BaseDevice;
import ru.avem.kspad.communication.devices.pm130.PM130Model;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class PM130ControllerIA extends BaseDevice {
    private static final short I1_REGISTER = 13318;

    private static final int CONVERT_BUFFER_SIZE = 4;
    private static final float I_DIVIDER = 100.f;
    private static final int I_MULTIPLIER = 1000;
    private static final int NUM_OF_WORDS_IN_REGISTER = 2;
    private static final short NUM_OF_REGISTERS = 1 * NUM_OF_WORDS_IN_REGISTER;

    public PM130ControllerIA(int modbusAddress, Observer observer, ModbusController controller) {
        address = (byte) modbusAddress;
        model = new PM130ModelIA(observer, address);
        modbusController = controller;
    }

    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (isThereAreReadAttempts()) {
            if (readAttempt >= 0) readAttempt--;
            ModbusController.RequestStatus status = modbusController.readInputRegisters(
                    address, I1_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                resetReadAttempts();
                model.setReadResponding(true);
                ((PM130ModelIA) model).setI1(convertUINTtoINT(inputBuffer.getInt()) * I_MULTIPLIER / I_DIVIDER / 1000);
            } else {
                read(args);
            }
        } else {
            if (readAttemptOfAttempt >= 0) readAttemptOfAttempt--;
            if (readAttemptOfAttempt <= 0) {
                model.setReadResponding(false);
                ((PM130ModelIA) model).setI1(0);
            } else {
                resetReadAttempts();
            }
        }
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

}