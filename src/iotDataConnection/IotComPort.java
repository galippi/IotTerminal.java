package iotDataConnection;

import java.util.Enumeration;
import java.util.Vector;

import IotTerminal.IotTerminalPrefs;
import gnu.io.CommPortIdentifier;
import lippiWare.utils.dbg;

public class IotComPort {

    public IotComPort(IotDataConnectionSerial _parent) {
        parent = _parent;
        comPortName = IotTerminalPrefs.get(comPortNamePref, "COM29");
        baudRate = IotTerminalPrefs.get(baudRatePref, 9600);
        pollingTime = IotTerminalPrefs.get(pollingTimePref, 100);
    }

    public static void reinit() {
        if (parent != null)
            parent.reinit();
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

    public static String[] getAvailablePorts() {
        @SuppressWarnings("unchecked")
        Enumeration<CommPortIdentifier> comPortsEnumeration = CommPortIdentifier.getPortIdentifiers();
        Vector<String> comPorts = new Vector<>();
        while(comPortsEnumeration.hasMoreElements()) {
            CommPortIdentifier cpi = comPortsEnumeration.nextElement();
            dbg.println(9, "IotComPort.getAvailablePorts.CommPortIdentifier.getPortIdentifiers cpi=" + cpi.toString() + " " + cpi.getName() + " " + cpi.getPortType());
            comPorts.add(cpi.getName());
        }
        return (String[]) comPorts.toArray(new String[comPorts.size()]);
    }

    static IotDataConnectionSerial parent;
    static final String comPortNamePref = "ComPortName";
    static String comPortName;
    static final String baudRatePref = "BaudRate";
    static int baudRate;
    static final String pollingTimePref = "PollingTime";
    static int pollingTime;
}
