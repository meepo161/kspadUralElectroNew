package ru.avem.kspad.communication.protocol.modbus;

import java.nio.ByteBuffer;

import ru.avem.kspad.communication.connection_protocols.Connection;
import ru.avem.kspad.communication.protocol.modbus.utils.CRC16;

public class RTUController implements ModbusController {
    private Connection mConnection;
    private final LogAnalyzer logAnalyzer;

    public RTUController(Connection connection) {
        mConnection = connection;
        logAnalyzer = new LogAnalyzer(connection.getName());
    }

    public RequestStatus reportSlaveID(byte deviceAddress, byte identifier, byte versionSoftware,
                                       byte versionHardware, int serialNumber,
                                       ByteBuffer inputBuffer) {
        ByteBuffer outputBuffer = ByteBuffer.allocate(11)
                .put(deviceAddress)
                .put(Command.REPORT_SLAVE_ID.getValue())
                .put(identifier)
                .put(versionSoftware)
                .put(versionHardware)
                .putInt(serialNumber);
        CRC16.sign(outputBuffer);
        return sendCommand(deviceAddress, Command.REPORT_SLAVE_ID.getValue(), outputBuffer, inputBuffer);
    }

    public RequestStatus readInputRegisters(byte deviceAddress, short registerAddress,
                                            short numberOfRegisters, ByteBuffer inputBuffer) {
        ByteBuffer outputBuffer = ByteBuffer.allocate(8)
                .put(deviceAddress)
                .put(Command.READ_INPUT_REGISTERS.getValue());
        if (numberOfRegisters != 0) {
            outputBuffer.putShort(registerAddress)
                    .putShort(numberOfRegisters);
        }
        CRC16.sign(outputBuffer);
        return sendCommand(deviceAddress, Command.READ_INPUT_REGISTERS.getValue(), outputBuffer, inputBuffer);
    }

    public RequestStatus writeSingleHoldingRegister(byte deviceAddress, short registerAddress,
                                                    byte[] data, ByteBuffer inputBuffer) {
        ByteBuffer outputBuffer = ByteBuffer.allocate(10)
                .put(deviceAddress)
                .put(Command.WRITE_SINGLE_HOLDING_REGISTER.getValue())
                .putShort(registerAddress)
                .put(data);
        CRC16.sign(outputBuffer);
        return sendCommand(deviceAddress, Command.WRITE_SINGLE_HOLDING_REGISTER.getValue(), outputBuffer, inputBuffer);
    }

    public RequestStatus readMultipleHoldingRegisters(byte deviceAddress, short registerAddress,
                                                      short numberOfRegisters,
                                                      ByteBuffer inputBuffer) {
        ByteBuffer outputBuffer = ByteBuffer.allocate(8)
                .put(deviceAddress)
                .put(Command.READ_MULTIPLE_HOLDING_REGISTERS.getValue());
        if (numberOfRegisters != 0) {
            outputBuffer.putShort(registerAddress)
                    .putShort(numberOfRegisters);
        }
        CRC16.sign(outputBuffer);
        return sendCommand(deviceAddress, Command.READ_MULTIPLE_HOLDING_REGISTERS.getValue(), outputBuffer, inputBuffer);
    }

    @Override
    public RequestStatus writeMultipleHoldingRegisters(byte deviceAddress, short registerAddress, short numberOfRegisters, ByteBuffer dataBuffer, ByteBuffer inputBuffer) {
        ByteBuffer outputBuffer = ByteBuffer.allocate(256)
                .put(deviceAddress)
                .put(Command.WRITE_MULTIPLE_HOLDING_REGISTER.getValue());
        if (numberOfRegisters != 0) {
            outputBuffer.putShort(registerAddress)
                    .putShort(numberOfRegisters)
                    .put((byte) (numberOfRegisters * 2))
                    .put(dataBuffer);
        }
        CRC16.sign(outputBuffer);
        return sendCommand(deviceAddress, Command.WRITE_MULTIPLE_HOLDING_REGISTER.getValue(), sliceBuffer(outputBuffer), inputBuffer);
    }

    private ByteBuffer sliceBuffer(ByteBuffer outputBuffer) {
        ByteBuffer slicedBuffer = ByteBuffer.allocate(outputBuffer.position());
        return slicedBuffer.put(outputBuffer.array(), 0, outputBuffer.position());
    }

    private synchronized RequestStatus sendCommand(byte deviceAddress, short command, ByteBuffer outputBuffer, ByteBuffer inputBuffer) {
        RequestStatus status = RequestStatus.UNKNOWN;
        try {

            int frameSize;
            byte inputArray[] = new byte[256];
            do {
                logAnalyzer.addWrite();
                mConnection.write(outputBuffer.array());

                if (deviceAddress != (byte) 1) {
                    int attempt = 0;
                    do {
                        frameSize = mConnection.read(inputArray);
                    } while ((frameSize < 6) && (++attempt < 3) && (frameSize != 1));
                } else {
                    frameSize = mConnection.read(inputArray);
                }
            } while (frameSize == 1);

            if (frameSize > 4 && (deviceAddress == inputArray[0]) &&
                    ((command == inputArray[1]) || ((command & 0x80) == inputArray[1]))) {
                if (CRC16.check(inputArray, frameSize)) {
                    if ((inputArray[1] & 0x80) == 0) {
                        logAnalyzer.addSuccess();
                        status = RequestStatus.FRAME_RECEIVED;
                        ((ByteBuffer) inputBuffer.clear()).put(inputArray, 0, frameSize).flip()
                                .position(3);
                    } else {
                        switch (inputArray[2]) {
                            case 0x01:
                                status = RequestStatus.BAD_FUNCTION;
                                break;
                            case 0x02:
                                status = RequestStatus.BAD_DATA_ADDS;
                                break;
                            case 0x03:
                                status = RequestStatus.BAD_DATA_VALUE;
                                break;
                            case 0x04:
                                status = RequestStatus.DEVICE_FAILURE;
                                break;
                            default:
                                status = RequestStatus.UNKNOWN;
                                break;
                        }
                    }
                } else {
                    status = RequestStatus.BAD_CRC;
                }
            } else {
                status = RequestStatus.UNKNOWN;
            }
            Thread.sleep(ModbusController.READ_DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return status;
    }
}