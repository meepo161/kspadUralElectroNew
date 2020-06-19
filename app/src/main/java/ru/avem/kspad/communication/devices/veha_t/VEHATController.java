package ru.avem.kspad.communication.devices.veha_t;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.BaseDevice;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class VEHATController extends BaseDevice {
    private static final short ROTATION_FREQUENCY_REGISTER = 1; // это конфигурация TH01 0x29

    private static final int NUM_OF_WORDS_IN_REGISTER = 2;
    private static final short NUM_OF_REGISTERS = 1 * NUM_OF_WORDS_IN_REGISTER;


    public VEHATController(int modbusAddress, Observer observer, ModbusController controller) {
        address = (byte) modbusAddress;
        model = new VEHATModel(observer, address);
        modbusController = controller;
    }

    @Override
    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (isThereAreReadAttempts()) {
            if (readAttempt >= 0) readAttempt--;
            ModbusController.RequestStatus status = modbusController.readInputRegisters(
//            ModbusController.RequestStatus status = mModbusController.readMultipleHoldingRegisters( это конфигурация TH01
                    address, ROTATION_FREQUENCY_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                model.setReadResponding(true);
                ((VEHATModel) model).setRotationFrequency(convertToMidLittleEndian(inputBuffer.getFloat()));
//                mModel.setRotationFrequency(inputBuffer.getInt()); это конфигурация TH01
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
}
