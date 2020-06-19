package ru.avem.kspad.communication.devices.pm130_ia;

import java.util.Observable;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceModel;

import static ru.avem.kspad.communication.devices.Device.PM130_ID;

public class PM130ModelIA extends Observable implements DeviceModel {
    public static final int RESPONDING_PARAM = 0;
    public static final int I1_PARAM = 1;

    private boolean readResponding = true;
    private boolean writeResponding = true;
    private final int deviceID;

    PM130ModelIA(Observer observer, int id) {
        deviceID = id;
        addObserver(observer);
    }



    void setI1(float i1) {
        notice(I1_PARAM, i1);
    }

    private void notice(int param, Object value) {
        setChanged();
        notifyObservers(new Object[]{deviceID, param, value});
    }

    @Override
    public void resetResponding() {
        readResponding = true;
        writeResponding = true;
    }

    @Override
    public void setReadResponding(boolean readResponding) {
        this.readResponding = readResponding;
        setResponding();
    }

    @Override
    public void setWriteResponding(boolean writeResponding) {
        this.writeResponding = writeResponding;
        setResponding();
    }

    private void setResponding() {
        notice(RESPONDING_PARAM, readResponding && writeResponding);
    }
}