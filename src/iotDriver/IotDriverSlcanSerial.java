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
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import iotDataConnection.IotComPort;
import lippiWare.utils.dbg;

class SlcanCanBaudRateSupported {
    static final int[] canBaudListInt = {125000, 250000, 500000};
    static final int[] canBaudListOption = {4, 5, 6};
    static final String[] canBaudListStr = {"125000", "250000", "500000"};

    static int getIdx(String canBaud) {
        for (int i = 0; i < canBaudListStr.length; i++) {
            if (canBaudListStr[i].contentEquals(canBaud))
                return i;
        }
        return -1;
    }

    static int getIdx(int canBaud) {
        for (int i = 0; i < canBaudListInt.length; i++) {
            if (canBaudListInt[i] == canBaud)
                return i;
        }
        return -1;
    }
}

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

        JLabel lbCanBaud = new JLabel("CAN baud rate:");
        canBaudBox = new JComboBox<>(SlcanCanBaudRateSupported.canBaudListStr);
        canBaudBox.setSelectedItem("" + parent.canBaud);

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
        add(lbCanBaud);
        add(canBaudBox);
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
            dbg.println(9, "IotSlcanConfigDlg.okHandler - not valid selection com Port baud=" + baud);
            errorMsg = "Not valid baud rate value=" + baud;
        }

        String canBaudStr = (String)canBaudBox.getSelectedItem();
        int canBaudVal = -1;
        try {
            canBaudVal = Integer.parseInt(canBaudStr);
            int canBaudIdx = SlcanCanBaudRateSupported.getIdx(canBaudVal);
            if (canBaudIdx < 0)
                throw new Exception("Invalid CAN baud rate value " + canBaudStr + "!");
        }catch(Exception e) {
            dbg.println(9, "IotSlcanConfigDlg.okHandler - not valid selection CAN baud=" + canBaudStr);
            errorMsg = "Not valid CAN baud rate value=" + canBaudStr;
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
        parent.canBaud = canBaudVal;

        IotTerminalPrefs.put("IotSlcanConfigDlgX", getX());
        IotTerminalPrefs.put("IotSlcanConfigDlgY", getY());
        IotTerminalPrefs.put("IotSlcanConfigDlgH", getHeight());
        IotTerminalPrefs.put("IotSlcanConfigDlgW", getWidth());
    }

    IotDriverSlcanSerial parent;
    private JComboBox<String> box;
    private JComboBox<String> canBaudBox;
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

    @Override
    public String getConfig() {
        return portName + ";" + baud + ";" + canBaud;
    }

    @Override
    public void setConfig(String configStr) throws Exception {
        String[] configArray = configStr.split(";");
        if (configArray.length != 3) {
            String msg = "IotDriverSlcanSerial.setConfig - number of parameters are wrong name=" + getName() + " configStr=" + configStr;
            dbg.println(9, msg);
            throw new Exception(msg);
        }
        portName = configArray[0];
        try {
            baud = Integer.parseUnsignedInt(configArray[1]);
        }catch (Exception e) {
            String msg = "IotDriverSlcanSerial.setConfig - wrong baud name=" + getName() + " baud=" + configArray[1] + " e=" + e.toString();
            dbg.println(9, msg);
            throw new Exception(msg);
        }
        try {
            canBaud = Integer.parseUnsignedInt(configArray[2]);
        }catch (Exception e) {
            String msg = "IotDriverSlcanSerial.setConfig - wrong CAN baud name=" + getName() + " canBaud=" + configArray[2] + " e=" + e.toString();
            dbg.println(9, msg);
            throw new Exception(msg);
        }
    }

    @Override
    public String checkDevice() {
        try
        {
            CommPortIdentifier portId =
                    CommPortIdentifier.getPortIdentifier(portName);
            SerialPort serialPort = (SerialPort) portId.open("IOT", 5000);
            serialPort.setSerialPortParams(
                baud,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
          dbg.println(19, "IotSlcanConfigDlg.checkDevice Before setFlowControlMode");
          serialPort.setFlowControlMode(
                      SerialPort.FLOWCONTROL_NONE);
          dbg.println(19, "IotSlcanConfigDlg.checkDevice After setFlowControlMode");
          java.io.OutputStream outStream = serialPort.getOutputStream();
          java.io.InputStream inStream = serialPort.getInputStream();
          outStream.write("V\n".getBytes());
          Thread.sleep(1000);
          byte[] response = new byte[2048];
          int len = inStream.read(response);
          outStream.close();
          inStream.close();
          serialPort.close();
          String responseStr = new String(response, 0, len);
          dbg.println(9, "IotSlcanConfigDlg.checkDevice len=" + len + " respone=" + responseStr);
          return responseStr;
        }catch (Exception e) {
            String errorMsg = "IotSlcanConfigDlg.checkDevice - exception e=" + e.toString();
            dbg.println(3, errorMsg);
            return errorMsg;
        }
    }

    String portName;
    int baud = 1200;
    int canBaud = 250000;
    IotDriverBaseConfigDlg dlg;
}
