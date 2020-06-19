package ru.avem.kspad.communication.devices.pm130;

import android.support.annotation.NonNull;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.BaseDevice;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class PM130Controller extends BaseDevice {
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

    public PM130Controller(int modbusAddress, Observer observer, ModbusController controller) {
        address = (byte) modbusAddress;
        model = new PM130Model(observer, address);
        modbusController = controller;
    }

    public void read(Object... args) {
        if (isThereAreReadAttempts()) {
            if (readAttempt >= 0) readAttempt--;
            switch ((Integer) args[0]) {
                case 1:
                    if (!getI().equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                        read(args);
                    } else {
                        resetReadAttempts();
                    }
                    break;
                case 2:
                    if (!getU().equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                        read(args);
                    } else {
                        resetReadAttempts();
                    }
                    break;
                case 3:
                    if (!getP().equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                        read(args);
                    } else {
                        resetReadAttempts();
                    }
                    break;
                case 4:
                    if (!getF().equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                        read(args);
                    } else {
                        resetReadAttempts();
                    }
                    break;
            }
        } else {
            if (readAttemptOfAttempt >= 0) readAttemptOfAttempt--;
            if (readAttemptOfAttempt <= 0) {
                model.setReadResponding(false);
                ((PM130Model) model).setV1(0);
                ((PM130Model) model).setV2(0);
                ((PM130Model) model).setV3(0);
                ((PM130Model) model).setI1(0);
                ((PM130Model) model).setI2(0);
                ((PM130Model) model).setI3(0);
                ((PM130Model) model).setP1(0);
                ((PM130Model) model).setS1(0);
                ((PM130Model) model).setCos(0);
                ((PM130Model) model).setF(0);
            } else {
                resetReadAttempts();
            }
        }
    }


    @NonNull
    private ModbusController.RequestStatus getI() {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        ModbusController.RequestStatus statusI = modbusController.readInputRegisters(
                address, I1_REGISTER, NUM_OF_REGISTERS, inputBuffer);
        if (statusI.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
            model.setReadResponding(true);
            try {
                ((PM130Model) model).setI1(convertUINTtoINT(inputBuffer.getInt()) * I_MULTIPLIER / I_DIVIDER / 1000);
                ((PM130Model) model).setI2(convertUINTtoINT(inputBuffer.getInt()) * I_MULTIPLIER / I_DIVIDER / 1000);
                ((PM130Model) model).setI3(convertUINTtoINT(inputBuffer.getInt()) * I_MULTIPLIER / I_DIVIDER / 1000);
            } catch (Exception ignored) {
            }
        }
        return statusI;
    }

    @NonNull
    private ModbusController.RequestStatus getU() {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        ModbusController.RequestStatus statusV = modbusController.readInputRegisters(
                address, VL1_REGISTER, NUM_OF_REGISTERS, inputBuffer);
        if (statusV.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
            model.setReadResponding(true);
            try {
                ((PM130Model) model).setV1(convertUINTtoINT(inputBuffer.getInt()) * U_MULTIPLIER / U_DIVIDER);
                ((PM130Model) model).setV2(convertUINTtoINT(inputBuffer.getInt()) * U_MULTIPLIER / U_DIVIDER);
                ((PM130Model) model).setV3(convertUINTtoINT(inputBuffer.getInt()) * U_MULTIPLIER / U_DIVIDER);
            } catch (Exception ignored) {
            }
        }
        return statusV;
    }

    @NonNull
    private ModbusController.RequestStatus getP() {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        ModbusController.RequestStatus statusP = modbusController.readInputRegisters(
                address, P_REGISTER, (short) 8, inputBuffer);
        if (statusP.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
            model.setReadResponding(true);
            try {
                ((PM130Model) model).setP1(convertMidEndianINTtoINT(inputBuffer.getInt()) / 1000.0f);
                inputBuffer.getInt();
                ((PM130Model) model).setS1(convertUINTtoINT(inputBuffer.getInt()) / 1000.0f);
                ((PM130Model) model).setCos(convertMidEndianINTtoINT(inputBuffer.getInt()) / 1000.0f);
            } catch (Exception ignored) {
            }
        }
        return statusP;
    }

    @NonNull
    private ModbusController.RequestStatus getF() {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        ModbusController.RequestStatus statusF = modbusController.readInputRegisters(
                address, F_REGISTER, (short) 2, inputBuffer);
        if (statusF.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
            model.setReadResponding(true);
            try {
                ((PM130Model) model).setF(convertUINTtoINT(inputBuffer.getInt()) / 100.0f);
            } catch (Exception ignored) {
            }
        }
        return statusF;
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
}