package iotDriver;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import IotTerminal.IotDeviceSetupDlg;
import IotTerminal.IotTerminalPrefs;
import iotDataConnection.IotComPort;
import lippiWare.utils.dbg;

class IotSlcanConfigDlg extends IotDriverBaseConfigDlg {
    IotSlcanConfigDlg(IotDriverSlcanSerial _parent) {
        super(IotDeviceSetupDlg.idsd, Dialog.ModalityType.APPLICATION_MODAL);
        parent = _parent;
        this.setTitle("Device setup");

        JLabel lbPort = new JLabel("COM port:");
        String[] ports = IotComPort.getAvailablePorts();
        box = new JComboBox<>(ports);
        box.setSelectedItem(parent.portName);

        JLabel lbBaud = new JLabel("Baud rate:");
        tBaud = new JTextField("" + parent.baud);

        JButton bOk = new JButton("OK");
        bOk.setHorizontalAlignment(SwingConstants.LEFT);
        bOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okHandler();
            }
        });

        JButton bCancel = new JButton("Cancel");
        bCancel.setHorizontalAlignment(SwingConstants.RIGHT);
        bCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dbg.println(9, "IotSlcanConfigDlg.Cancel.actionPerformed e=" + e.toString());
                setVisible(false);
            }
        });

        JPanel bOkCancel = new JPanel();
        bOkCancel.add(bOk);
        bOkCancel.add(bCancel);

        setLayout(new FlowLayout());

        add(lbPort);
        add(box);
        add(lbBaud);
        add(tBaud);
        add(bOkCancel);
        pack();
        
        setLocation(IotTerminalPrefs.get("IotSlcanConfigDlgX", 0), IotTerminalPrefs.get("IotSlcanConfigDlgY", 0));
        setSize(IotTerminalPrefs.get("IotSlcanConfigDlgW", 200), IotTerminalPrefs.get("IotSlcanConfigDlgH", 150));
        this.setMinimumSize(new Dimension(200, 100));
    }

    private void okHandler() {
        dbg.println(9, "IotSlcanConfigDlg.okHandler.actionPerformed");
        setVisible(false);
        String errorMsg = null;

        String comPortName = (String)box.getSelectedItem();
        if ((comPortName.length() < 1) || (!IotComPort.isComPortValid(comPortName))) {
            dbg.println(9, "IotSlcanConfigDlg.okHandler - not valid selection comPortName=" + comPortName);
            errorMsg = "Not valid selection comPortName=" + comPortName;
        }

        String baud = tBaud.getText();
        int baudVal = -1;
        try {
            baudVal = Integer.parseInt(baud);
            if ((baudVal < 100) || (baudVal > 999999))
                throw new Exception("Invalid baud rate value " + baud + "!");
        }catch(Exception e) {
            dbg.println(9, "IotSlcanConfigDlg.okHandler - not valid selection comPortName=" + comPortName);
            errorMsg = "Not valid baud rate value=" + baud;
        }

        if (errorMsg != null) {
            dbg.println(9, "IotSlcanConfigDlg.okHandler - not valid selection comPortName=" + comPortName);
            JOptionPane.showMessageDialog(IotDeviceSetupDlg.idsd, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
            setVisible(true);
            return;
        }

        dbg.println(9, "IotSlcanConfigDlg.okHandler - valid selection comPortName=" + comPortName + " baud=" + baudVal);
        parent.portName = comPortName;
        parent.baud = baudVal;

        IotTerminalPrefs.put("IotSlcanConfigDlgX", getX());
        IotTerminalPrefs.put("IotSlcanConfigDlgY", getY());
        IotTerminalPrefs.put("IotSlcanConfigDlgH", getHeight());
        IotTerminalPrefs.put("IotSlcanConfigDlgW", getWidth());
    }

    IotDriverSlcanSerial parent;
    private JComboBox<String> box;
    private JTextField tBaud;

    private static final long serialVersionUID = 9090788248731657889L;
}

public class IotDriverSlcanSerial implements IotDriverBase {

    @Override
    public int getSubchannelNumber() {
        return 1;
    }

    @Override
    public void start() throws Exception {
        throw new Exception("IotDriverSlcanSerial - Not yet implemented portName=" + portName);
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void init(int channelIdx, Object initData) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void checkConfig(Vector<IotDriverBase> driverList) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public IotDriverBase create() {
        return new IotDriverSlcanSerial();
    }

    @Override
    public String getName() {
        return "Slcan Serial";
    }

    @Override
    public IotDriverBaseConfigDlg getConfigDlg() {
        if (dlg == null)
            dlg = new IotSlcanConfigDlg(this);
        return dlg;
    }

    String portName;
    int baud = 1200;
    IotDriverBaseConfigDlg dlg;
}
