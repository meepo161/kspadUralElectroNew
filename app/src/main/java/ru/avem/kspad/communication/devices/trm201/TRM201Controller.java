package ru.avem.kspad.communication.devices.trm201;

import java.nio.ByteBuffer;
import java.util.Observer;

import ru.avem.kspad.communication.devices.BaseDevice;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;

public class TRM201Controller  extends BaseDevice {
    private static final short T_AMBIENT_REGISTER = 1;

    private static final int NUM_OF_WORDS_IN_REGISTER = 1;
    private static final short NUM_OF_REGISTERS = 2 * NUM_OF_WORDS_IN_REGISTER;


    public TRM201Controller(int modbusAddress, Observer observer, ModbusController controller) {
        address = (byte) modbusAddress;
        model = new TRM201Model(observer, address);
        modbusController = controller;
    }

    @Override
    public void read(Object... args) {
        ByteBuffer inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
        if (isThereAreReadAttempts()) {
            if (readAttempt >= 0) readAttempt--;
            ModbusController.RequestStatus status = modbusController.readMultipleHoldingRegisters(
                    address, T_AMBIENT_REGISTER, NUM_OF_REGISTERS, inputBuffer);
            if (status.equals(ModbusController.RequestStatus.FRAME_RECEIVED)) {
                model.setReadResponding(true);
                resetReadAttempts();
               ((TRM201Model) model).setTAmbient(inputBuffer.getShort() / 10f);
               ((TRM201Model) model).setTEngine(inputBuffer.getShort() / 10f);
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