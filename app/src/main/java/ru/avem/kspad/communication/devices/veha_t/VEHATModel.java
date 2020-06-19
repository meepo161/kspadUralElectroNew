package ru.avem.kspad.communication.devices.veha_t;

import java.util.Observable;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceModel;

import static ru.avem.kspad.communication.devices.Device.VEHA_T_ID;

public class VEHATModel extends Observable implements DeviceModel {
    public static final int RESPONDING_PARAM = 0;
    public static final int ROTATION_FREQUENCY_PARAM = 1;

    private boolean readResponding = true;
    private boolean writeResponding = true;
    private final int deviceID;

    VEHATModel(Observer observer, int id) {
        deviceID = id;
        addObserver(observer);
    }

    public void setRotationFrequency(float rotationFrequency) {
        notice(ROTATION_FREQUENCY_PARAM, rotationFrequency);
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