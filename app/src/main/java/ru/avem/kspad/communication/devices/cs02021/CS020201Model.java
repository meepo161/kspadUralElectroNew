package ru.avem.kspad.communication.devices.cs02021;

import java.util.Observable;
import java.util.Observer;

import static ru.avem.kspad.communication.devices.DeviceController.MEGGER_ID;

public class CS020201Model extends Observable {
    public static final int RESPONDING_PARAM = 0;

    CS020201Model(Observer observer) {
        addObserver(observer);
    }

    void setResponding(boolean responding) {
        notice(RESPONDING_PARAM, responding);
    }

    private void notice(int param, Object value) {
        setChanged();
        notifyObservers(new Object[]{MEGGER_ID, param, value});
    }
}
