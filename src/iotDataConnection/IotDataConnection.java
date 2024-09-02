package iotDataConnection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import IotTerminal.IotTerminalMain;
import lippiWare.utils.dbg;

public class IotDataConnection implements ActionListener {
    public IotDataConnection(IotTerminalMain iotTerminalMain) {
        parent = iotTerminalMain;
        t = new Timer(100, this);
        t.setRepeats(false);
        t.start();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        dbg.println(9, "IotDataConnection.actionPerformed");
        parent.addLog("Rx: " + "bruhaha");
        //t.setDelay(1000);
        t.setInitialDelay(1000);
        //t.restart();
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
}
