package ru.avem.kspad.communication.devices.beckhoff;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceController;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class BeckhoffController implements DeviceController {
    private static final byte MODBUS_ADDRESS = 1;
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

    private BeckhoffModel mModel;
    private ModbusController mModbusProtocol;
    private byte mAttempt = NUMBER_OF_ATTEMPTS;
    private boolean mNeedToReed;

    public BeckhoffController(Observer observer, ModbusController controller) {
        mModel = new BeckhoffModel(observer);
        mModbusProtocol = controller;
    }

    @Override
    public void read(Object... args) {
//        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
//        if (thereAreAttempts()) {
//            mAttempt--;
//            ModbusController.RequestStatus status = mModbusProtocol.readInputRegisters(
//                    MODBUS_ADDRESS, STATUS_REGISTER, NUM_OF_REGISTERS, inputBuffer);
//            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
//                mModel.setResponding(true);
//                resetAttempts();
//                mModel.setStatus(inputBuffer.getShort());
//            } else {
//                read(args);
//            }
//        } else {
//            mModel.setResponding(false);
//            mModel.setStatus((short) 0);
//        }
        readStatus();
        readTriggers();
    }

    private void readStatus() {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (thereAreAttempts()) {
            mAttempt--;
            ModbusController.RequestStatus status = mModbusProtocol.readInputRegisters(
                    MODBUS_ADDRESS, STATUS_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                mModel.setResponding(true);
                resetAttempts();
                mModel.setStatus(inputBuffer.getShort());
            } else {
                readStatus();
            }
        } else {
            mModel.setResponding(false);
        }
    }

    private void readTriggers() {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (thereAreAttempts()) {
            mAttempt--;
            ModbusController.RequestStatus status = mModbusProtocol.readInputRegisters(
                    MODBUS_ADDRESS, TRIGGERS_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                mModel.setResponding(true);
                resetAttempts();
                mModel.setTriggers(inputBuffer.getShort());
            } else {
                readTriggers();
            }
        } else {
            mModel.setResponding(false);
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
                    MODBUS_ADDRESS, register, (short) numOfRegisters, dataBuffer, inputBuffer);
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