package ru.avem.kspad.communication.devices.pm130_ia;

import java.util.Observable;
import java.util.Observer;

import static ru.avem.kspad.communication.devices.DeviceController.PM130_ID;

public class PM130ModelIA extends Observable {
    public static final int RESPONDING_PARAM = 0;
    public static final int I1_PARAM = 1;

    PM130ModelIA(Observer observer) {
        addObserver(observer);
    }

    void setResponding(boolean responding) {
        notice(RESPONDING_PARAM, responding);
    }

    void setI1(float i1) {
        notice(I1_PARAM, i1);
    }

    private void notice(int param, Object value) {
        setChanged();
        notifyObservers(new Object[]{PM130_ID, param, value});
    }
}