package iotDriver;

import java.awt.Window;

import javax.swing.JDialog;

import IotTerminal.IotDriverBaseConfigDlgCallback;

public class IotDriverBaseConfigDlg extends JDialog {
    public IotDriverBaseConfigDlg(Window parent, ModalityType applicationModal) {
        super(parent, applicationModal);
    }

    public void run(IotDriverBaseConfigDlgCallback parent) {
        this.caller = parent;
        setVisible(true);
    }

    IotDriverBaseConfigDlgCallback caller;

    private static final long serialVersionUID = 6518860741368430463L;
}
