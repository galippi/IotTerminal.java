package iotDriver;

public class IotDriverDataBase {
    public IotDriverDataBase(byte[] _data) {
        timeStamp_us = java.lang.System.nanoTime() / 1000;
        data = _data;
    }

    public IotDriverDataBase(long _timeStamp_us, byte[] _data) {
        timeStamp_us = _timeStamp_us;
        data = _data;
    }

    long timeStamp_us;
    byte[] data;
}
