package iotDriver;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import IotTerminal.IotDeviceSetupDlg;
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
        add(bOkCancel);
        pack();
    }

    private void okHandler() {
        dbg.println(9, "IotSlcanConfigDlg.okHandler.actionPerformed");
        setVisible(false);
        String comPortName = (String)box.getSelectedItem();
        if ((comPortName.length() < 1) || (!IotComPort.isComPortValid(comPortName))) {
            // TODO
            dbg.println(9, "IotSlcanConfigDlg.okHandler - not valid selection comPortName=" + comPortName);
        }else {
            dbg.println(9, "IotSlcanConfigDlg.okHandler - valid selection comPortName=" + comPortName);
            parent.portName = comPortName;
        }
    }

    IotDriverSlcanSerial parent;
    private JComboBox<String> box;

    private static final long serialVersionUID = 9090788248731657889L;
}

public class IotDriverSlcanSerial implements IotDriverBase {

    @Override
    public int getSubchannelNumber() {
        return 1;
    }

    @Override
    public void start() throws Exception {
        // TODO Auto-generated method stub
        
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
    IotDriverBaseConfigDlg dlg;
}
