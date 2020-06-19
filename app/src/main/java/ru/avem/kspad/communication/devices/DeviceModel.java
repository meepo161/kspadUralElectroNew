package ru.avem.kspad.communication.devices;

public interface DeviceModel {
    void resetResponding();

    void setReadResponding(boolean readResponding);

    void setWriteResponding(boolean writeResponding);
}
