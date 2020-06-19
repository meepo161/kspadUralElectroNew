package ru.avem.kspad.communication.devices.FR_A800;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.BaseDevice;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class FRA800Controller extends BaseDevice {
    public static final short CONTROL_REGISTER = 8;
    public static final short CURRENT_FREQUENCY_REGISTER = 14;
    public static final short MAX_FREQUENCY_REGISTER = 1002;
    public static final short MAX_VOLTAGE_REGISTER = 1018;

    private static final int NUM_OF_WORDS_IN_REGISTER = 1;
    private static final short NUM_OF_REGISTERS = 1 * NUM_OF_WORDS_IN_REGISTER;


    public FRA800Controller(int modbusAddress, Observer observer, ModbusController controller) {
        address = (byte) modbusAddress;
        model = new FRA800Model(observer, address);
        modbusController = controller;
    }

    @Override
    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (isThereAreReadAttempts()) {
            if (readAttempt >= 0) readAttempt--;
            ModbusController.RequestStatus status = modbusController.readMultipleHoldingRegisters(
                    address, CONTROL_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                model.setReadResponding(true);
                ((FRA800Model) model).setControlState(inputBuffer.getShort());
                resetReadAttempts();
            } else {
                read(args);
            }
        } else {
            if (readAttemptOfAttempt >= 0) readAttemptOfAttempt--;
            if (readAttemptOfAttempt <= 0) {
                model.setReadResponding(false);
            } else {
                resetReadAttempts();
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

        if (isThereAreWriteAttempts()) {
            if (writeAttempt >= 0) writeAttempt--;
            ModbusController.RequestStatus status = modbusController.writeMultipleHoldingRegisters(
                    address, register, (short) numOfRegisters, dataBuffer, inputBuffer);
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
}