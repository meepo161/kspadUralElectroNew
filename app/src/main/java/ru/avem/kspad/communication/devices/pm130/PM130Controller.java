package ru.avem.kspad.communication.devices.pm130;

import android.support.annotation.NonNull;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceController;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class PM130Controller implements DeviceController {
    private static final byte MODBUS_ADDRESS = 0x1E;

//    private static final short I1_REGISTER = 13318; мгновенные
//    private static final short VL1_REGISTER = 13372;
//    private static final short P_REGISTER = 13696;

    private static final short I1_REGISTER = 13958; // за 1 секунду
    private static final short VL1_REGISTER = 14012;
    private static final short P_REGISTER = 14336;
    private static final short F_REGISTER = 14468;

    private static final int CONVERT_BUFFER_SIZE = 4;
    private static final float U_DIVIDER = 10.f;
    private static final int U_MULTIPLIER = 1;
    private static final float I_DIVIDER = 100.f;
    private static final int I_MULTIPLIER = 1000;
    private static final int NUM_OF_WORDS_IN_REGISTER = 2;
    private static final short NUM_OF_REGISTERS = 3 * NUM_OF_WORDS_IN_REGISTER;

    private PM130Model mModel;
    private ModbusController mModbusController;
    private byte mAttempt = NUMBER_OF_ATTEMPTS;
    private boolean mNeedToReed;

    public PM130Controller(Observer observer, ModbusController modbusController) {
        mModel = new PM130Model(observer);
        mModbusController = modbusController;
    }

    public void read(Object... args) {
        if (thereAreAttempts()) {
            mAttempt--;
            switch ((Integer) args[0]) {
                case 1:
                    if (!getI().equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                        read(args);
                    } else {
                        resetAttempts();
                    }
                    break;
                case 2:
                    if (!getU().equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                        read(args);
                    } else {
                        resetAttempts();
                    }
                    break;
                case 3:
                    if (!getP().equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                        read(args);
                    } else {
                        resetAttempts();
                    }
                    break;
                case 4:
                    if (!getF().equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                        read(args);
                    } else {
                        resetAttempts();
                    }
                    break;
            }
        } else {
            mModel.setResponding(false);
            mModel.setV1(0);
            mModel.setV2(0);
            mModel.setV3(0);
            mModel.setI1(0);
            mModel.setI2(0);
            mModel.setI3(0);
            mModel.setP1(0);
            mModel.setS1(0);
            mModel.setCos(0);
            mModel.setF(0);
        }
    }

    @NonNull
    private ModbusController.RequestStatus getI() {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        ModbusController.RequestStatus statusI = mModbusController.readInputRegisters(
                MODBUS_ADDRESS, I1_REGISTER, NUM_OF_REGISTERS, inputBuffer);
        if (statusI.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
            mModel.setResponding(true);
            try {// TODO: 12.12.2017 сделать нормально
                mModel.setI1(convertUINTtoINT(inputBuffer.getInt()) * I_MULTIPLIER / I_DIVIDER / 1000);
                mModel.setI2(convertUINTtoINT(inputBuffer.getInt()) * I_MULTIPLIER / I_DIVIDER / 1000);
                mModel.setI3(convertUINTtoINT(inputBuffer.getInt()) * I_MULTIPLIER / I_DIVIDER / 1000);
            } catch (Exception ignored) {
            }
        }
        return statusI;
    }

    @NonNull
    private ModbusController.RequestStatus getU() {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        ModbusController.RequestStatus statusV = mModbusController.readInputRegisters(
                MODBUS_ADDRESS, VL1_REGISTER, NUM_OF_REGISTERS, inputBuffer);
        if (statusV.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
            mModel.setResponding(true);
            try {// TODO: 12.12.2017 сделать нормально
                mModel.setV1(convertUINTtoINT(inputBuffer.getInt()) * U_MULTIPLIER / U_DIVIDER);
                mModel.setV2(convertUINTtoINT(inputBuffer.getInt()) * U_MULTIPLIER / U_DIVIDER);
                mModel.setV3(convertUINTtoINT(inputBuffer.getInt()) * U_MULTIPLIER / U_DIVIDER);
            } catch (Exception ignored) {
            }
        }
        return statusV;
    }

    @NonNull
    private ModbusController.RequestStatus getP() {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        ModbusController.RequestStatus statusP = mModbusController.readInputRegisters(
                MODBUS_ADDRESS, P_REGISTER, (short) 8, inputBuffer);
        if (statusP.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
            mModel.setResponding(true);
            try {// TODO: 12.12.2017 сделать нормально
                mModel.setP1(convertMidEndianINTtoINT(inputBuffer.getInt()) / 1000.0f);
                inputBuffer.getInt();
                mModel.setS1(convertUINTtoINT(inputBuffer.getInt()) / 1000.0f);
                mModel.setCos(convertMidEndianINTtoINT(inputBuffer.getInt()) / 1000.0f);
            } catch (Exception ignored) {
            }
        }
        return statusP;
    }

    @NonNull
    private ModbusController.RequestStatus getF() {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        ModbusController.RequestStatus statusF = mModbusController.readInputRegisters(
                MODBUS_ADDRESS, F_REGISTER, (short) 2, inputBuffer);
        if (statusF.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
            mModel.setResponding(true);
            try {// TODO: 12.12.2017 сделать нормально
                mModel.setF(convertUINTtoINT(inputBuffer.getInt()) / 100.0f);
            } catch (Exception ignored) {
            }
        }
        return statusF;
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

    private int convertMidEndianINTtoINT(int i) {
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
        return convertBuffer.getInt();
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