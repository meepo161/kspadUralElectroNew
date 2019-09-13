package ru.avem.kspad.communication.devices.m40;

import java.util.Observable;
import java.util.Observer;

import static ru.avem.kspad.communication.devices.DeviceController.M40_ID;

public class M40Model extends Observable {
    public static final int RESPONDING_PARAM = 0;
    public static final int TORQUE_PARAM = 1;
//    public static final int ROTATION_FREQUENCY_PARAM = 2;

    M40Model(Observer observer) {
        addObserver(observer);
    }

    void setResponding(boolean responding) {
        notice(RESPONDING_PARAM, responding);
    }

    void setTorque(float torque) {
        notice(TORQUE_PARAM, torque);
    }

//    void setRotationFrequency(float rotationFrequency) {
//        notice(ROTATION_FREQUENCY_PARAM, rotationFrequency);
//    }

    private void notice(int param, Object value) {
        setChanged();
        notifyObservers(new Object[]{M40_ID, param, value});
    }
}