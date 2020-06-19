package ru.avem.kspad.communication.devices.trm201;

import java.util.Observable;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceModel;

import static ru.avem.kspad.communication.devices.Device.TRM201_ID;

public class TRM201Model extends Observable implements DeviceModel {
    public static final int RESPONDING_PARAM = 0;
    public static final int T_AMBIENT_PARAM = 1;
    public static final int T_ENGINE_PARAM = 2;

    private boolean readResponding = true;
    private boolean writeResponding = true;
    private final int deviceID;

    TRM201Model(Observer observer, int id) {
        deviceID = id;
        addObserver(observer);
    }

    public void setTAmbient(float t) {
        notice(T_AMBIENT_PARAM, t);
    }

    public void setTEngine(float t) {
        notice(T_ENGINE_PARAM, t);
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