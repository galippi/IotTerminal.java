package iotDriver;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import IotTerminal.IotDeviceSetupDlg;
import IotTerminal.IotTerminalPrefs;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import iotDataConnection.IotComPort;
import lippiWare.blfHandler.CanMessage;
import lippiWare.blfHandler.VectorAscFile;
import lippiWare.utils.dbg;
import lwLogDataProcessor.LogDataProcessor;
import lwLogDataProcessor.LogDataProcessorDefaultHandler;
import lwLogDataProcessor.LogDataProcessorHandlerBase;

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

        addEscapeListener();
    }

    void addEscapeListener() {
        ActionListener escListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dbg.println(9, "IotSlcanConfigDlg.addEscapeListener.actionPerformed");
                dispose();
            }
        };
        getRootPane().registerKeyboardAction(escListener,
              KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
              JComponent.WHEN_IN_FOCUSED_WINDOW);
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

        dbg.println(9, "IotSlcanConfigDlg.okHandler - valid selection comPortName=" + comPortName + " baud=" + baudVal + " canBaudVal=" + canBaudVal);
        parent.portName = comPortName;
        parent.baud = baudVal;
        parent.canBaud = canBaudVal;

        IotTerminalPrefs.put("IotSlcanConfigDlgX", getX());
        IotTerminalPrefs.put("IotSlcanConfigDlgY", getY());
        IotTerminalPrefs.put("IotSlcanConfigDlgH", getHeight());
        IotTerminalPrefs.put("IotSlcanConfigDlgW", getWidth());

        caller.IotDriverBaseConfigDlgCallbackOkHandler();
    }

    IotDriverSlcanSerial parent;
    private JComboBox<String> box;
    private JComboBox<String> canBaudBox;
    private JTextField tBaud;

    private static final long serialVersionUID = 9090788248731657889L;
}

class IotCanMsg extends IotDriverDataBase {
    public IotCanMsg(int chId, int id, byte[] data) {
        super(data);
        timeStamp_us = java.lang.System.nanoTime() / 1000;
        msg = new CanMessage(chId, timeStamp_us * 1000, id, CanMessage.Rx, data);
    }

    public String toString() {
        return VectorAscFile.toString(msg);
    }

    CanMessage msg;
}

interface IotCanMessageReceiver {
    void msgProcess(IotCanMsg msg);
}

class SlcanDataProcessorStd extends LogDataProcessorHandlerBase {
    public SlcanDataProcessorStd(IotCanMessageReceiver parent, int chIdx) {
        super("t");
        this.parent = parent;
        this.chIdx = chIdx;
    }

    @Override
    public void process(String data) throws Exception {
        int msgId = hex2dec(data, 1, 3);
        int dlc = hex2dec(data, 4, 1);
        if (data.length() != (1 + 3 + 1 + dlc * 2))
            throw new Exception("SlcanDataProcessorStd.process exception dlc error: " + dlc + "<->" + data.length() + " data=" + data + "!");
        byte[] dataBuf = null;
        if (dlc > 0) {
            dataBuf = new byte[dlc];
            hex2decArray(data, 5, dataBuf, 0, dlc);
        }
        IotCanMsg msg = new IotCanMsg(chIdx, msgId, dataBuf);
        parent.msgProcess(msg);
    }

    IotCanMessageReceiver parent;
    private int chIdx;
}

class SlcanDataProcessorExt extends LogDataProcessorHandlerBase {

    public SlcanDataProcessorExt(IotCanMessageReceiver parent, int chIdx) {
        super("T");
        this.parent = parent;
        this.chIdx = chIdx;
    }

    @Override
    public void process(String data) throws Exception {
        int msgId = hex2dec(data, 1, 8) | CanMessage.ExtId;
        int dlc = hex2dec(data, 9, 1);
        if (data.length() != (1 + 8 + 1 + dlc * 2))
            throw new Exception("SlcanDataProcessorStd.process exception dlc error: " + dlc + "<->" + data.length() + " data=" + data + "!");
        byte[] dataBuf = null;
        if (dlc > 0) {
            dataBuf = new byte[dlc];
            hex2decArray(data, 10, dataBuf, 0, dlc);
        }
        IotCanMsg msg = new IotCanMsg(chIdx, msgId, dataBuf);
        parent.msgProcess(msg);
    }

    IotCanMessageReceiver parent;
    private int chIdx;
}

class IotDriverSlcanSerialProcess implements Runnable, IotCanMessageReceiver {
    IotDriverSlcanSerialProcess(IotDriverSlcanSerial parent, int chIdx) {
        this.parent = parent;
        parent.dc.putDebug("IotDriverSlcanSerialProcess.ctor " + parent.portName);
        ldp = new LogDataProcessor();
        ldp.addHandler(new SlcanDataProcessorStd(this, chIdx));
        ldp.addHandler(new SlcanDataProcessorExt(this, chIdx));
    }

    @Override
    public void run() {
        parent.dc.putDebug("IotDriverSlcanSerialProcess.run " + parent.portName);
        while(!toBeStopped) {
            try {
                int len;
                while ((len = parent.inStream.read(response)) > 0) {
                    slcanProcess(response, len);
                }
                String msg;
                while ((msg = messages.poll()) != null) {
                    parent.outStream.write(msg.getBytes());
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    parent.dc.putDebug("IotDriverSlcanSerialProcess.run.sleep exception e=" + e.toString());
                }
            } catch (IOException e) {
                String errorMsg = "IotDriverSlcanSerialProcess.run exception e=" + e.toString();
                parent.dc.putDebug(errorMsg);
            }
        }
        stopped = true;
    }

    boolean rxMessageIsInSync;
    String rxMessageRest = "";

    void slcanProcess(byte[] data, int num) {
        String rxMessage = new String(data, 0, num);
        String msg = parent.portName + ": " + num + "-> "+ rxMessage;
        parent.dc.putDebug(msg);
        //String rxMessage = new String(data, 0, num, Charset.forName("US-ASCII"));
        //addLog(rxMessage);
        rxMessage = rxMessage.replace('\r', '\n');
        if (!rxMessageIsInSync) {
            int idx = rxMessage.indexOf('\n');
            if (idx < 0)
                return;
            rxMessage = rxMessage.substring(idx + 1);
            rxMessageRest = "";
            rxMessageIsInSync = true;
        }
        rxMessageRest = rxMessageRest + rxMessage;
        int idx;
        while ((idx = rxMessageRest.indexOf('\n')) >= 0) {
            if (idx > 0) {
                rxMessage = rxMessageRest.substring(0, idx);
                dbg.println(11, "Rx:" + rxMessage);
                ldp.process(rxMessage, defaultHandler);
            }
            rxMessageRest = rxMessageRest.substring(idx + 1);
        }
    }


    @Override
    public void msgProcess(IotCanMsg msg) {
        parent.dc.putData(msg);
    }

    LogDataProcessor ldp;
    LogDataProcessorDefaultHandler defaultHandler = new LogDataProcessorDefaultHandler();
    IotDriverSlcanSerial parent;
    boolean toBeStopped = false;
    boolean stopped = false;
    byte[] response = new byte[2048];
    ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();
}

public class IotDriverSlcanSerial extends IotDriverBase {

    private Thread thread;

    @Override
    public int getSubchannelNumber() {
        return 1;
    }

    @Override
    public void start(IotDriverDataCollector dc, int chIdx) throws Exception {
        dbg.println(9, "IotDriverSlcanSerial.start port=" + portName);
        this.dc = dc;
        openPort();
        int canBaudIdx = SlcanCanBaudRateSupported.getIdx(canBaud);
        if (canBaudIdx < 0) {
            String errorMsg = "IotDriverSlcanSerial.openPort - invalid canBaud=" + canBaud;
            dbg.println(3, errorMsg);
            throw new Exception(errorMsg);
        }
        outStream.write(("S" + SlcanCanBaudRateSupported.canBaudListOption[canBaudIdx] + "\nO\n").getBytes());
        sp = new IotDriverSlcanSerialProcess(this, chIdx);
        thread = new Thread(sp);
        thread.start();
    }
    IotDriverSlcanSerialProcess sp;

    @Override
    public void stop() {
        dbg.println(9, "IotDriverSlcanSerial.stop port=" + portName);
        if (sp == null)
            return;
        sp.toBeStopped = true;
        while(!sp.stopped) {
            dbg.println(3, "IotDriverSlcanSerial.stopping");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                dbg.println(3, "IotDriverSlcanSerial.stop.sleep exception e=" + e.toString());
            }
        }
        dc = null;
        if (outStream != null) {
            try {
                outStream.close();
            } catch (IOException e) {
                dbg.println(3, "IotDriverSlcanSerial.stop - outStream.close exception e=" + e.toString());
            }
            outStream = null;
        }
        if (inStream != null) {
            try {
                inStream.close();
            } catch (IOException e) {
                dbg.println(3, "IotDriverSlcanSerial.stop - inStream.close exception e=" + e.toString());
            }
            inStream = null;
        }
        serialPort.close();
    }

    @Override
    public void close() {
        dbg.println(9, "IotDriverSlcanSerial.close port=" + portName);
        stop();
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

    void openPort() throws Exception {
        try
        {
            CommPortIdentifier portId =
                    CommPortIdentifier.getPortIdentifier(portName);
            serialPort = (SerialPort) portId.open("IOT", 5000);
            serialPort.setSerialPortParams(
                baud,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
          dbg.println(19, "IotDriverSlcanSerial.openPort Before setFlowControlMode");
          serialPort.setFlowControlMode(
                      SerialPort.FLOWCONTROL_NONE);
          dbg.println(19, "IotDriverSlcanSerial.openPort After setFlowControlMode");
          outStream = serialPort.getOutputStream();
          inStream = serialPort.getInputStream();
          outStream.write("C\nV\n".getBytes());
        }catch (Exception e) {
            String errorMsg = "IotDriverSlcanSerial.openPort - exception e=" + e.toString();
            dbg.println(3, errorMsg);
            throw new Exception(errorMsg);
        }
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
    IotDriverDataCollector dc;
    SerialPort serialPort;
    java.io.OutputStream outStream;
    java.io.InputStream inStream;
}
