package ru.avem.kspad.communication.connection_protocols;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import ru.avem.kspad.utils.Logger;

public class EthernetConnection implements Connection {
    private static final String TAG = "StatusActivity";

    private String mConnectionIP;
    private int mConnectionPort;

    private Socket mPort;

    public EthernetConnection(String connectionIP, int connectionPort) {
        mConnectionIP = connectionIP;
        mConnectionPort = connectionPort;
    }

    @Override
    public boolean initConnection() {
        try {
            mPort = new Socket(mConnectionIP, mConnectionPort);
            Logger.withTag("DEBUG_TAG").log("mPort = port");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.withTag("DEBUG_TAG").log("mPort != port");
        }

        return false;
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
    public int write(byte[] outputArray) {
        int numBytesWrite = outputArray.length;
        try {
            if (mPort != null) {
                OutputStream out = mPort.getOutputStream();
                out.write(outputArray);
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
                InputStream in = mPort.getInputStream();
                numBytesRead = in.read(inputArray);
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
