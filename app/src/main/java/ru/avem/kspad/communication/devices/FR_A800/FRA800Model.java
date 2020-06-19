package ru.avem.kspad.communication.devices.FR_A800;

import java.util.Observable;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceModel;

public class FRA800Model extends Observable implements DeviceModel {
    public static final int RESPONDING_PARAM = 0;
    public static final int READY_PARAM = 1;

    private boolean readResponding = true;
    private boolean writeResponding = true;
    private final int deviceID;

    FRA800Model(Observer observer, int id) {
        deviceID = id;
        addObserver(observer);
    }



    void setControlState(short controlState) {
//        notice(KM1_PARAM, (sta & 0b1) == 0);
//        notice(DOOR_S_PARAM, (sta & 0b10) == 0);
//        notice(PARAM, (sta & 0b100) > 0);
        notice(READY_PARAM, (controlState & 0b1000) > 0);
//        notice(OBJ_1_PARAM, (sta & 0b10000) == 0);
//        notice(OBJ_2_PARAM, (sta & 0b100000) == 0);
//        notice(OBJ_3_PARAM, (sta & 0b1000000) == 0);
//        notice(OBJ_4_PARAM, (sta & 0b10000000) == 0);
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