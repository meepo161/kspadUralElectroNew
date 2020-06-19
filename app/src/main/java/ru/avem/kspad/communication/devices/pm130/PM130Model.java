package ru.avem.kspad.communication.devices.pm130;

import java.util.Observable;
import java.util.Observer;

import ru.avem.kspad.communication.devices.DeviceModel;

import static ru.avem.kspad.communication.devices.Device.PM130_ID;

public class PM130Model extends Observable implements DeviceModel {
    public static final int RESPONDING_PARAM = 0;
    public static final int V1_PARAM = 1;
    public static final int V2_PARAM = 2;
    public static final int V3_PARAM = 3;
    public static final int I1_PARAM = 4;
    public static final int I2_PARAM = 5;
    public static final int I3_PARAM = 6;
    public static final int P_PARAM = 7;
    public static final int S_PARAM = 8;
    public static final int COS_PARAM = 9;
    public static final int F_PARAM = 10;

    private boolean readResponding = true;
    private boolean writeResponding = true;
    private final int deviceID;

    PM130Model(Observer observer, int id) {
        deviceID = id;
        addObserver(observer);
    }

    void setV1(float v1) {
        if (v1 != 0) {
            notice(V1_PARAM, v1);
        }
    }

    void setV2(float v2) {
        if (v2 != 0) {
            notice(V2_PARAM, v2);
        }
    }

    void setV3(float v3) {
        if (v3 != 0) {
            notice(V3_PARAM, v3);
        }
    }

    void setI1(float i1) {
        if (i1 != 0) {
            notice(I1_PARAM, i1);
        }
    }

    void setI2(float i2) {
        if (i2 != 0) {
            notice(I2_PARAM, i2);
        }
    }

    void setI3(float i3) {
        if (i3 != 0) {
            notice(I3_PARAM, i3);
        }
    }

    void setP1(float p1) {
        notice(P_PARAM, p1);
    }

    void setS1(float s1) {
        notice(S_PARAM, s1);
    }

    void setCos(float cos) {
        notice(COS_PARAM, cos);
    }

    void setF(float f) {
        notice(F_PARAM, f);
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