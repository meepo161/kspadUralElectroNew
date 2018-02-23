package ru.avem.kspad.communication.devices.cs02021;

import android.content.Context;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import ru.avem.kspad.communication.protocol.modbus.utils.CRC16;
import ru.avem.kspad.communication.serial.SerialConnection;

public class CS02021Controller {
    private static final int WRITE_TIMEOUT = 100;
    private static final int READ_TIMEOUT = 100;
    private static final int BAUD_RATE = 9600;

    private final SerialConnection mConnection;

    public CS02021Controller(Context context) {
        mConnection = new SerialConnection(context, "CP2103 USB to Megger", BAUD_RATE,
                UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE,
                WRITE_TIMEOUT, READ_TIMEOUT);
        try {
            mConnection.initSerialPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SerialConnection getConnection() {
        return mConnection;
    }

    public synchronized void setVoltage(int u) {
        byte byteU = (byte) (u / 10);
        ByteBuffer outputBuffer = ByteBuffer.allocate(5)
                .put((byte) 0x04)
                .put((byte) 0x01)
                .put(byteU);
        CRC16.signReversWithSlice(outputBuffer);
//                .put((byte) 0x47)
//                .put((byte) 0x3F);
        mConnection.write(outputBuffer.array());
        byte inputArray[] = new byte[40];
        ByteBuffer inputBuffer = ByteBuffer.allocate(40);
        int attempt = 0;
        do {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int frameSize = mConnection.read(inputArray);
            inputBuffer.put(inputArray, 0, frameSize);
        } while (inputBuffer.position() < 5 && (++attempt < 10));
    }

    public synchronized float[] readData() {
        float[] data = new float[4];
        ByteBuffer outputBuffer = ByteBuffer.allocate(5)
                .put((byte) 0x04)
                .put((byte) 0x07)
                .put((byte) 0x71)
                .put((byte) 0x64)
                .put((byte) 0x7F);
        mConnection.write(outputBuffer.array());
        byte inputArray[] = new byte[40];
        ByteBuffer inputBuffer = ByteBuffer.allocate(40);
        ByteBuffer finalBuffer = ByteBuffer.allocate(40);
        int attempt = 0;
        do {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int frameSize = mConnection.read(inputArray);
            inputBuffer.put(inputArray, 0, frameSize);
        } while (inputBuffer.position() < 16 && (++attempt < 10));
        Log.i("TAG", "bytes: " + Arrays.toString(inputBuffer.array()));
        if (inputBuffer.position() == 16/* && checkSum(inputBuffer.array())*/) {
            inputBuffer.flip().position(2);
            finalBuffer.put(inputBuffer.get());
            finalBuffer.put(inputBuffer.get());
            finalBuffer.put(inputBuffer.get());
            finalBuffer.put((byte) 0);
            finalBuffer.put(inputBuffer.get());
            finalBuffer.put(inputBuffer.get());
            finalBuffer.put(inputBuffer.get());
            finalBuffer.put((byte) 0);
            finalBuffer.put(inputBuffer.get());
            finalBuffer.put(inputBuffer.get());
            finalBuffer.put(inputBuffer.get());
            finalBuffer.put((byte) 0);
            finalBuffer.put(inputBuffer.get());
            finalBuffer.put(inputBuffer.get());
            finalBuffer.put(inputBuffer.get());
            finalBuffer.put((byte) 0);
            finalBuffer.flip();
            data[0] = finalBuffer.getFloat();
            data[1] = finalBuffer.getFloat();
            data[2] = finalBuffer.getFloat();
            data[3] = finalBuffer.getFloat();
        }
        return data;
    }

    private boolean checkSum(byte[] bytes) {
        int sum = 0;
        for (int i = 0; i < 16; i++) {
            sum += bytes[i];
            Log.i("TAG", "checkSum: " + sum + " " + bytes[i]);
        }
        if (bytes[16] >= 0) {
            sum &= 0xFF;
        }
        Log.i("TAG", "checkSum: " + sum + " " + bytes[16]);
        return sum == bytes[16];
    }
}
