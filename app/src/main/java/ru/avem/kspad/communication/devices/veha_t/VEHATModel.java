package ru.avem.kspad.communication.devices.veha_t;

import java.util.Observable;
import java.util.Observer;

import static ru.avem.kspad.communication.devices.DeviceController.VEHA_T_ID;

public class VEHATModel extends Observable {
    public static final int RESPONDING_PARAM = 0;
    public static final int ROTATION_FREQUENCY_PARAM = 1;

    VEHATModel(Observer observer) {
        addObserver(observer);
    }

    public void setResponding(boolean responding) {
        notice(RESPONDING_PARAM, responding);
    }

    public void setRotationFrequency(float rotationFrequency) {
        notice(ROTATION_FREQUENCY_PARAM, rotationFrequency);
    }

    private void notice(int param, Object value) {
        setChanged();
        notifyObservers(new Object[]{VEHA_T_ID, param, value});
    }
}