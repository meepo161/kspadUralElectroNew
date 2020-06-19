package ru.avem.kspad.communication.devices.ikas;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.BaseDevice;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class IKASController extends BaseDevice {
    private static final short STATUS_REGISTER = 0;
    public static final short MEASURABLE_TYPE_REGISTER = 0x65;
    public static final short START_MEASURABLE_REGISTER = 0x64;
    public static final int MEASURABLE_TYPE_AB = 0x46;
    public static final int MEASURABLE_TYPE_BC = 0x44;
    public static final int MEASURABLE_TYPE_AC = 0x45;

    private static final int NUM_OF_WORDS_IN_REGISTER = 1;
    private static final short NUM_OF_REGISTERS = 2 * NUM_OF_WORDS_IN_REGISTER;


    public IKASController(int modbusAddress, Observer observer, ModbusController controller) {
        address = (byte) modbusAddress;
        model = new IKASModel(observer, address);
        modbusController = controller;
    }

    @Override
    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (isThereAreReadAttempts()) {
            if (readAttempt >= 0) readAttempt--;
            ModbusController.RequestStatus status = modbusController.readInputRegisters(
                    address, STATUS_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                model.setReadResponding(true);
                resetReadAttempts();
                ((IKASModel) model).setReady(inputBuffer.getFloat());
                ((IKASModel) model).setMeasurable(inputBuffer.getFloat());
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
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (isThereAreWriteAttempts()) {
            if (writeAttempt >= 0) writeAttempt--;
            byte[] value = new byte[1];
            if (args[1] instanceof Integer) {
                value = intToByteArray((int) args[1]);
            } else if (args[1] instanceof Float) {
                value = floatToByteArray((float) args[1]);
            }
            ModbusController.RequestStatus status = modbusController.writeSingleHoldingRegister(address,
                    (short) args[0], value, inputBuffer);
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

    private byte[] intToByteArray(int i) {
        ByteBuffer convertBuffer = ByteBuffer.allocate(4);
        convertBuffer.clear();
        return convertBuffer.putInt(i).array();
    }

    private byte[] floatToByteArray(float f) {
        ByteBuffer convertBuffer = ByteBuffer.allocate(4);
        convertBuffer.clear();
        return convertBuffer.putFloat(f).array();
    }
}