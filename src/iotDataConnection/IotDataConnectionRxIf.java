package iotDataConnection;

public interface IotDataConnectionRxIf {
    void rxCallback(byte[] data, int num);
}