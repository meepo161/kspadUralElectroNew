package ru.avem.kspad.communication.connection_protocols;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;

import ru.avem.kspad.utils.Logger;

import static ru.avem.kspad.communication.devices.DevicesController.ACTION_USB_PERMISSION;

public class SerialConnection implements Connection {
    private static final String TAG = "StatusActivity";

    private final Context mContext;
    private UsbManager mUsbManager;
    private String mProductName;
    private int mBaudRate;
    private int mDataBits;
    private int mStopBits;
    private int mParity;
    private int mWriteTimeout;
    private int mReadTimeout;

    private UsbSerialPort mPort;

    public SerialConnection(Context context, String productName, int baudRate, int dataBits,
                            int stopBits, int parity, int writeTimeout, int readTimeout) {
        mContext = context;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mProductName = productName;
        mBaudRate = baudRate;
        mDataBits = dataBits;
        mStopBits = stopBits;
        mParity = parity;
        mWriteTimeout = writeTimeout;
        mReadTimeout = readTimeout;
    }

    @Override
    public boolean initConnection() {
        UsbSerialDriver usbSerialDriver = getSerialDriver();
        if (usbSerialDriver != null) {
            UsbSerialPort port = usbSerialDriver.getPorts().get(0);
            UsbDeviceConnection usbConnection = getUsbConnection(usbSerialDriver);
            if (usbConnection != null) {
                try {
                    port.open(usbConnection);
                    port.setParameters(mBaudRate, mDataBits, mStopBits, mParity);
                    mPort = port;
                    Logger.withTag("DEBUG_TAG").log("mPort = port");
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.withTag("DEBUG_TAG").log("mPort != port");
                }
            } else {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private UsbSerialDriver getSerialDriver() {
        List<UsbSerialDriver> availableDrivers =
                UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
        if (availableDrivers.isEmpty()) {
            return null;
        }
        for (UsbSerialDriver availableDriver : availableDrivers) {
            if (availableDriver.getDevice().getProductName().equals(mProductName)) {
                return availableDriver;
            }
        }
        return null;
    }

    private UsbDeviceConnection getUsbConnection(UsbSerialDriver usbSerialDriver) {
        UsbDevice device = usbSerialDriver.getDevice();
        UsbDeviceConnection connection = mUsbManager.openDevice(device);
        if (connection == null) {
            PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
            mUsbManager.requestPermission(device, pi);
            return null;
        }
        return connection;
    }


    @Override
    public void closeConnection() {
        try {
            if (mPort != null) {
                mPort.close();
            }
            mPort = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toHexString(byte[] src) {
        return toHexString(src, src.length);
    }

    private String toHexString(byte[] src, int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String s = Integer.toHexString(src[i] & 0xFF);
            if (s.length() < 2) {
                builder.append(0);
            }
            builder.append(s).append(' ');
        }
        return builder.toString().toUpperCase().trim();
    }

    @Override
    public String getName() {
        return mProductName;
    }

    @Override
    public int write(byte[] outputArray) {
        int numBytesWrite = 0;
        try {
            if (mPort != null) {
                numBytesWrite = mPort.write(outputArray, mWriteTimeout);
            } else {
                Log.i(TAG, "mPort null");
            }
            Log.i(TAG, "Write " + numBytesWrite + " bytes.");
            Log.i(TAG, "Write " + toHexString(outputArray));
            ru.avem.kspad.utils.Log.addLine("Write " + numBytesWrite + " bytes.");
            ru.avem.kspad.utils.Log.addLine("Write " + toHexString(outputArray) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numBytesWrite;
    }

    @Override
    public int read(byte[] inputArray) {
        int numBytesRead = 0;
        try {
            if (mPort != null) {
                numBytesRead = mPort.read(inputArray, mReadTimeout);
            } else {
                Log.i(TAG, "mPort null");
            }
            Log.i(TAG, "Read " + numBytesRead + " bytes.");
            Log.i(TAG, "Read: " + toHexString(inputArray, numBytesRead));
            ru.avem.kspad.utils.Log.addLine("Read " + numBytesRead + " bytes.");
            ru.avem.kspad.utils.Log.addLine("Read: " + toHexString(inputArray, numBytesRead) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numBytesRead;
    }


    @Override
    public boolean isInitiatedConnection() {
        return mPort != null;
    }
}
