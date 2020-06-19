package ru.avem.kspad.communication.connection_protocols;

public interface Connection {
    String getName();

    int write(byte[] outputArray);

    int read(byte[] inputArray);

    boolean isInitiatedConnection();

    boolean initConnection();

    void closeConnection();
}
