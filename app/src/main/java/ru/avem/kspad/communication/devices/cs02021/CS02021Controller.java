package ru.avem.kspad.communication.devices.cs02021;

import android.content.Context;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Observer;

import ru.avem.kspad.communication.connection_protocols.Connection;
import ru.avem.kspad.communication.connection_protocols.SerialConnection;
import ru.avem.kspad.communication.devices.DeviceController;
import ru.avem.kspad.communication.protocol.modbus.utils.CRC16;
import ru.avem.kspad.utils.Logger;

public class CS02021Controller implements DeviceController {
    private static final String TAG = "CS02021Controller";
    private static final int WRITE_TIMEOUT = 100;
    private static final int READ_TIMEOUT = 100;
    private static final int BAUD_RATE = 9600;

    private final Connection mConnection;
    private byte mAddress;
    private boolean mExperimentRun;

    private CS020201Model mModel;
    private boolean mNeedToReed;

    public CS02021Controller(Context context, byte address, Observer observer) {
        mConnection = new SerialConnection(context, "CP2103 USB to Megger", BAUD_RATE,
                UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE,
                WRITE_TIMEOUT, READ_TIMEOUT);
        mAddress = address;
        initSerialPort();

        mModel = new CS020201Model(observer);
    }

    public CS02021Controller(byte address, Observer observer, Connection connection) {
        mConnection = connection;
        mAddress = address;
        initSerialPort();

        mModel = new CS020201Model(observer);
    }

    public void initSerialPort() {
        mConnection.initConnection();
    }

    public void closeSerialPort() {
        mConnection.closeConnection();
    }

    public synchronized void setVoltage(int u) {
        byte byteU = (byte) (u / 10);
        ByteBuffer outputBuffer = ByteBuffer.allocate(5)
                .put(mAddress)
                .put((byte) 0x01)
                .put(byteU);
        CRC16.signReversWithSlice(outputBuffer);
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
                .put(mAddress)
                .put((byte) 0x07)
                .put((byte) 0x71)
                .put((byte) 0x64)
                .put((byte) 0x7F);

        ByteBuffer inputBuffer = ByteBuffer.allocate(40);
        ByteBuffer finalBuffer = ByteBuffer.allocate(40);

        while (mExperimentRun) {
            Logger.withTag("StatusActivity").log("mExperimentRun=" + mExperimentRun);
            inputBuffer.clear();
            mConnection.write(outputBuffer.array());
            byte inputArray[] = new byte[40];
            int attempt = 0;
            do {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int frameSize = mConnection.read(inputArray);
                inputBuffer.put(inputArray, 0, frameSize);
            } while (inputBuffer.position() < 16 && (++attempt < 15));
            if (attempt < 15) {
                break;
            }
        }

        Log.i("TAG", "bytes: " + Arrays.toString(inputBuffer.array()));
        if (inputBuffer.position() == 16) {
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

    public synchronized boolean isResponding() {
        ByteBuffer outputBuffer = ByteBuffer.allocate(5)
                .put(mAddress)
                .put((byte) 0x07)
                .put((byte) 0x71)
                .put((byte) 0x64)
                .put((byte) 0x7F);

        ByteBuffer inputBuffer = ByteBuffer.allocate(40);

        inputBuffer.clear();
        int writtenBytes = mConnection.write(outputBuffer.array());
        Logger.withTag(TAG).log("writtenBytes=" + writtenBytes);
        byte inputArray[] = new byte[40];
        int attempt = 0;
        do {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int frameSize = mConnection.read(inputArray);
            inputBuffer.put(inputArray, 0, frameSize);
        } while (inputBuffer.position() < 16 && (++attempt < 15));
        return inputBuffer.position() >= 16;
    }

    public void setExperimentRun(boolean experimentRun) {
        mExperimentRun = experimentRun;
        Logger.withTag("mExperimentRun").log(experimentRun);
    }

    @Override
    public void read(Object... args) {
        mModel.setResponding(isResponding());
    }

    @Override
    public void write(Object... args) {

    }

    @Override
    public void resetAttempts() {

    }

    @Override
    public boolean thereAreAttempts() {
        return false;
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
