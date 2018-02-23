package ru.avem.kspad.communication.devices.beckhoff;

import java.util.Observable;
import java.util.Observer;

import static ru.avem.kspad.communication.devices.DeviceController.BECKHOFF_CONTROL_ID;

public class BeckhoffModel extends Observable {
    public static final int RESPONDING_PARAM = 0;
    public static final int START_PARAM = 1;
    public static final int DOOR_S_PARAM = 2;
    public static final int I_PROTECTION_OBJECT_PARAM = 3;
    public static final int I_PROTECTION_VIU_PARAM = 4;
    public static final int I_PROTECTION_IN_PARAM = 5;
    public static final int DOOR_Z_PARAM = 6;

    BeckhoffModel(Observer observer) {
        addObserver(observer);
    }

    void setResponding(boolean responding) {
        notice(RESPONDING_PARAM, responding);
    }

    void setStatus(short status) {
        notice(START_PARAM, (status & 0b1) > 0);
        notice(DOOR_S_PARAM, (status & 0b10) > 0);
        notice(I_PROTECTION_OBJECT_PARAM, (status & 0b100) > 0);
        notice(I_PROTECTION_VIU_PARAM, (status & 0b1000) > 0);
        notice(I_PROTECTION_IN_PARAM, (status & 0b10000) > 0);
        notice(DOOR_Z_PARAM, (status & 0b100000) > 0);
    }

    private void notice(int param, Object value) {
        setChanged();
        notifyObservers(new Object[]{BECKHOFF_CONTROL_ID, param, value});
    }
}