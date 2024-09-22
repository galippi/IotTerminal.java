package iotDataConnection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Timer;

import IotTerminal.IotTerminalMain;
import lippiWare.utils.dbg;

class IotDataDemoDataRecord {
    public IotDataDemoDataRecord(int delay, String msg) {
        this.delay = delay;
        data = msg;
    }
    int delay;
    String data;
}

class IotDataDemo {
    IotDataDemo() {
        add(1000, "U0200");
        add(0500, "I01A5");
        add(1000, "U0170");
        add(0500, "I0220");
        add(1000, "U0150");
        add(0500, "I0250");
    }

    private void add(int delay, String msg) {
        data.add(new IotDataDemoDataRecord(delay, msg));
    }

    IotDataDemoDataRecord get() {
        if (data.size() <= 0)
            return null;
        IotDataDemoDataRecord result = data.get(0);
        data.removeElementAt(0);
        return result;
    }

    Vector<IotDataDemoDataRecord> data = new Vector<>();
}

public class IotDataConnection implements ActionListener, IotDataConnectionIf {
    public IotDataConnection(IotTerminalMain iotTerminalMain) {
        parent = iotTerminalMain;
        t = new Timer(0, this);
        t.setRepeats(false);
        scheduleMessage();
        t.start();
    }

    private void scheduleMessage() {
        IotDataDemoDataRecord data = idd.get();
        if (data != null) {
            //t.setDelay(1000);
            t.setInitialDelay(data.delay);
            rxMessage = data.data;
            t.restart();
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        dbg.println(9, "IotDataConnection.actionPerformed");
        parent.addLog("Rx: " + rxMessage);
        parent.processRxMessage(rxMessage);
        scheduleMessage();
    }

    public void close() {
        t.stop();
    }

    public void sendIotCommand(String cmd) {
        dbg.println(9, "IotDataConnection.sendIotCommand cmd=" + cmd);
        parent.addLog("Tx: " + cmd);
    }

    Timer t;
    IotTerminalMain parent;
    IotDataDemo idd = new IotDataDemo();
    private String rxMessage;
}
