package ru.avem.kspad.communication.devices.beckhoff;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.BaseDevice;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class BeckhoffController extends BaseDevice {
    public static final short STATUS_REGISTER = 16404;
    public static final short MOD_1_REGISTER = 16405;
    public static final short MOD_2_REGISTER = 16406;
    public static final short MOD_3_REGISTER = 16407;
    public static final short TRIGGERS_REGISTER = 16408;
    public static final short RESET_TRIGGERS_REGISTER = 16409;
    public static final short LIGHT_REGISTER = 16410;
    public static final short SOUND_REGISTER = 16411;
    public static final short RESET_CONNECTION_REGISTER = 16412;
    public static final short WATCH_DOG_REGISTER = 16413;

    private static final int NUM_OF_WORDS_IN_REGISTER = 1;
    private static final short NUM_OF_REGISTERS = 1 * NUM_OF_WORDS_IN_REGISTER;


    public BeckhoffController(int deviceID, Observer observer, ModbusController controller) {
        address = (byte) deviceID;
        model = new BeckhoffModel(observer, address);
        modbusController = controller;
    }

    @Override
    public void read(Object... args) {
        readStatus();
        readTriggers();
    }

    private void readStatus() {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (isThereAreReadAttempts()) {
            if (readAttempt >= 0) readAttempt--;
            ModbusController.RequestStatus status = modbusController.readInputRegisters(
                    address, STATUS_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                model.setReadResponding(true);
                resetReadAttempts();
                ((BeckhoffModel) model).setStatus(inputBuffer.getShort());
            } else {
                readStatus();
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

    private void readTriggers() {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (isThereAreReadAttempts()) {
            if (readAttempt >= 0) readAttempt--;
            ModbusController.RequestStatus status = modbusController.readInputRegisters(
                    address, TRIGGERS_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                model.setReadResponding(true);
                resetReadAttempts();
                ((BeckhoffModel) model).setTriggers(inputBuffer.getShort());
            } else {
                readTriggers();
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