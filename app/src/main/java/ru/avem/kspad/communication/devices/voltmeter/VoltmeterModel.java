package ru.avem.kspad.communication.devices.voltmeter;

import java.util.Observable;
import java.util.Observer;

import static ru.avem.kspad.communication.devices.DeviceController.VOLTMETER_ID;

public class VoltmeterModel extends Observable {
    public static final int RESPONDING_PARAM = 0;
    public static final int U_PARAM = 1;

    VoltmeterModel(Observer observer) {
        addObserver(observer);
    }

    public void setResponding(boolean responding) {
        notice(RESPONDING_PARAM, responding);
    }

    public void setU(float u) {
        notice(U_PARAM, u);
    }

    private void notice(int param, Object value) {
        setChanged();
        notifyObservers(new Object[]{VOLTMETER_ID, param, value});
    }
}