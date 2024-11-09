package iotDataConnection;

import IotTerminal.IotTerminalPrefs;

public class IotComPort {

    public IotComPort(IotDataConnectionSerial _parent) {
        parent = _parent;
        comPortName = IotTerminalPrefs.get(comPortNamePref, "COM29");
        baudRate = IotTerminalPrefs.get(baudRatePref, 9600);
        pollingTime = IotTerminalPrefs.get(pollingTimePref, 100);
    }

    public static void reinit() {
        // TODO Auto-generated method stub
        
    }

    public static String getPortName() {
        return comPortName;
    }

    public static int getBaudRate() {
        return baudRate;
    }

    public static int getPollingTime() {
        return pollingTime;
    }

    public static boolean isComPortValid(String comPortName) {
        return true;
    }

    public static void openPort(String comPortName) {
        IotComPort.comPortName = comPortName;
        IotTerminalPrefs.put(comPortNamePref, comPortName);
    }

    public static void setBaudRate(int baudRate) {
        IotComPort.baudRate = baudRate;
        IotTerminalPrefs.put(baudRatePref, baudRate);
    }

    public static void setPollingTime(int pollingTime) {
        IotComPort.pollingTime = pollingTime;
        IotTerminalPrefs.put(pollingTimePref, pollingTime);
    }

    static IotDataConnectionSerial parent;
    static final String comPortNamePref = "ComPortName";
    static String comPortName;
    static final String baudRatePref = "BaudRate";
    static int baudRate;
    static final String pollingTimePref = "PollingTime";
    static int pollingTime;
}
