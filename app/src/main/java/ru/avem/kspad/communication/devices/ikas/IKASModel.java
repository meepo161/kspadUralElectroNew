package ru.avem.kspad.communication.devices.ikas;

import java.util.Observable;
import java.util.Observer;

import static ru.avem.kspad.communication.devices.DeviceController.IKAS_ID;

public class IKASModel extends Observable {
    public static final int RESPONDING_PARAM = 0;
    public static final int READY_PARAM = 1;
    public static final int MEASURABLE_PARAM = 2;

    IKASModel(Observer observer) {
        addObserver(observer);
    }

    public void setResponding(boolean responding) {
        notice(RESPONDING_PARAM, responding);
    }

    public void setReady(float ready) {
        notice(READY_PARAM, ready);
    }

    public void setMeasurable(float measurable) {
        notice(MEASURABLE_PARAM, measurable);
    }

    private void notice(int param, Object value) {
        setChanged();
        notifyObservers(new Object[]{IKAS_ID, param, value});
    }
}