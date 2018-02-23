package ru.avem.kspad.communication.devices.trm201;

import java.util.Observable;
import java.util.Observer;

import static ru.avem.kspad.communication.devices.DeviceController.TRM201_ID;

public class TRM201Model extends Observable {
    public static final int RESPONDING_PARAM = 0;
    public static final int T_AMBIENT_PARAM = 1;
    public static final int T_ENGINE_PARAM = 2;

    TRM201Model(Observer observer) {
        addObserver(observer);
    }

    public void setResponding(boolean responding) {
        notice(RESPONDING_PARAM, responding);
    }

    public void setTAmbient(float t) {
        notice(T_AMBIENT_PARAM, t);
    }

    public void setTEngine(float t) {
        notice(T_ENGINE_PARAM, t);
    }

    private void notice(int param, Object value) {
        setChanged();
        notifyObservers(new Object[]{TRM201_ID, param, value});
    }
}