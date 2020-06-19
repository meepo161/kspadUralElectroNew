package ru.avem.kspad.communication.devices.voltmeter;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.BaseDevice;
import ru.avem.kspad.communication.devices.FR_A800.FRA800Model;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class VoltmeterController extends BaseDevice {
    private static final short U_RMS_REGISTER = 2;

    private static final int NUM_OF_WORDS_IN_REGISTER = 1;
    private static final short NUM_OF_REGISTERS = 1 * NUM_OF_WORDS_IN_REGISTER;


    public VoltmeterController(int modbusAddress, Observer observer, ModbusController controller) {
        address = (byte) modbusAddress;
        model = new VoltmeterModel(observer, address);
        modbusController = controller;
    }

    @Override
    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (isThereAreReadAttempts()) {
            if (readAttempt >= 0) readAttempt--;
            ModbusController.RequestStatus status = modbusController.readInputRegisters(
                    address, U_RMS_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                model.setReadResponding(true);
                ((VoltmeterModel) model).setU(inputBuffer.getFloat());
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
}