package ru.avem.kspad.communication.devices.cs02021;

import java.util.Observable;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceModel;

import static ru.avem.kspad.communication.devices.Device.MEGGER_ID;

public class CS020201Model extends Observable implements DeviceModel {
    public static final int RESPONDING_PARAM = 0;

    private boolean readResponding = true;
    private boolean writeResponding = true;

    CS020201Model(Observer observer) {
        addObserver(observer);
    }



    private void notice(int param, Object value) {
        setChanged();
        notifyObservers(new Object[]{MEGGER_ID, param, value});
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
