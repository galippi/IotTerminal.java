package IotTerminal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import config.DbgConfig;
import lippiWare.utils.bin;
import lippiWare.utils.dbg;
import lwLogDataProcessor.LogDataProcessorHandlerBase;
import lwLogDataProcessor.LogDataProcessorHexu8ArrayBase;

interface IotGaugePanelIf
{
    void setLogDataProcessorHandler(LogDataProcessorHandlerBase _ldph);
    LogDataProcessorHandlerBase getLogDataProcessorHandler();
}

abstract class IotGaugePanel extends JPanel implements IotGaugePanelIf
{
    public void setLogDataProcessorHandler(LogDataProcessorHandlerBase _ldph)
    {
        ldph = _ldph;
    }

    public LogDataProcessorHandlerBase getLogDataProcessorHandler()
    {
        return ldph;
    }

    private LogDataProcessorHandlerBase ldph;

    private static final long serialVersionUID = 5041857164464118261L;
}

interface IotGaugeValueChangeCallback
{
    void setValue(String type, double val);
}

class IotGaugeVoltage extends IotGaugePanel implements ActionListener {
    public IotGaugeVoltage(String comPrefix, double _factor, double _offset, IotGaugeValueChangeCallback _vcc)
    {
        setLogDataProcessorHandler(new IotVoltageHandler(comPrefix, _factor, _offset, this));
        vcc = _vcc;
        t = new Timer(1000, this); // timeout in ms
        t.setRepeats(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setValue(Double.NaN);
    }

    public void setValue(double val) {
        dbg.println(19, "IotGaugeVoltage.setValue val=" + val);
        if (val < 0.001)
            this.val = 0;
        else
            this.val = val;
        this.repaint();
        if (Double.isNaN(this.val))
            t.stop();
        else
            t.restart();
        if (vcc != null) {
            LogDataProcessorHandlerBase ldh = getLogDataProcessorHandler();
            vcc.setValue(ldh.getPrefix(), this.val);
        }
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        dbg.println(DbgConfig.dbgLevelMaskGui | 9, "IotGaugeVoltage - paintComponent " + (Object)this);
        //java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
        int diagHeight = getHeight();
        int diagWidth = getWidth();
        Font fontDefault = g.getFont();
        g.setColor(new Color(255, 70, 0));
        g.fillRect(0, 0, diagWidth, diagHeight);
        g.setColor(Color.BLACK);
        g.drawRoundRect(0, 0, diagWidth - 1, diagHeight - 1, 15, 15);
        //g.drawRect(0, 0, diagWidth, diagHeight);
        g.setColor(Color.BLUE);
        int fontSize = ((diagWidth < diagHeight) ? diagWidth : diagHeight) / 2;
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        String valStr;
        if (Double.isNaN(val)) {
            valStr = "NaN      ";
        }else{
            valStr = "" + val;
            if (valStr.length() > 5)
                valStr = valStr.substring(0, 5);
            else
            if (valStr.length() < 5)
                valStr = valStr + "00000".substring(valStr.length(), 5);
            valStr = valStr + " V";
        }
        g.drawString(valStr, 30, diagHeight - 20);

        g.setColor(Color.BLACK);
        g.setFont(fontDefault);
        g.drawString("ctr=" + paintCtr, 30, 70);
        paintCtr++;
    }

    protected double val = Double.NaN;
    int paintCtr = 0;
    Timer t;
    IotGaugeValueChangeCallback vcc;

    private static final long serialVersionUID = 364126482051139014L;
}

class IotGaugeCurrent extends IotGaugeVoltage {
    public IotGaugeCurrent(String comPrefix, double factor, double offset, IotGaugeValueChangeCallback ibp) {
        super(comPrefix, factor, offset, ibp);
    }

    //@Override
    public void setValue_(double val) {
        this.val = val;
        this.repaint();
        t.restart();
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        //super.paintComponent(g);
        dbg.println(DbgConfig.dbgLevelMaskGui | 9, "IotGaugeCurrent - paintComponent " + (Object)this);

        int diagHeight = getHeight();
        int diagWidth = getWidth();
        Font fontDefault = g.getFont();
        g.setColor(new Color(255, 130, 0));
        g.fillRect(0, 0, diagWidth, diagHeight);
        g.setColor(Color.BLACK);
        g.drawRoundRect(0, 0, diagWidth - 1, diagHeight - 1, 15, 15);
        //g.drawRect(0, 0, diagWidth, diagHeight);
        g.setColor(Color.BLUE);
        int fontSize = ((diagWidth < diagHeight) ? diagWidth : diagHeight) / 2;
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));

        String valStr;
        if (Double.isNaN(val)) {
            valStr = "NaN       ";
        }else{
            double valSigned = val;
            if (valSigned > 0x7FFF)
                valSigned = valSigned - 0x10000;
            //valStr = "" + (int)(valSigned * (3.3 * 1000 / 2.7 / 4096));
            valStr = "" + (int)(valSigned);
            //valStr = "" + (int)valSigned;
            if (valStr.length() > 5)
                valStr = valStr.substring(0, 5);
            else
            if (valStr.length() < 5)
                valStr = "     ".substring(valStr.length(), 5) + valStr;
            valStr = valStr + " mA";
        }
        g.drawString(valStr, 30, diagHeight - 20);

        g.setColor(Color.BLACK);
        g.setFont(fontDefault);
        g.drawString("ctr=" + paintCtr, 30, 70);
        paintCtr++;
    }

    private static final long serialVersionUID = 9068897958698386929L;
}

interface IotGaugeDhtChangeCallback
{
    void setValue(String type, double T, double hum, int dataCtr, int errCtr);
}

class DhtCallBack implements IotGaugeDhtChangeCallback {
    @Override
    public void setValue(String type, double T, double hum, int dataCtr, int errCtr) {
        IotDataLogger.setDHT(T, hum, dataCtr, errCtr);
    }
}

class IotGaugeDht11 extends IotGaugePanel {
    public IotGaugeDht11(String _comPrefix, IotGaugeDhtChangeCallback _dhtCallBack) {
        setLogDataProcessorHandler(new IotDht11Handler("DHT", this));
        dhtCallBack = _dhtCallBack;
    }

    public void setValue(byte[] data) {
        this.data = data;
        if ((data != null) && (data.length == 6)) {
            t         = toInt(data[0]) * 256 + toInt(data[1]);
            hummidity = toInt(data[2]) * 256 + toInt(data[3]);
            dataCtr   = toInt(data[4]);
            errCtr    = toInt(data[5]);
        }else {
            t = -32768;
            hummidity = 65535;
        }
        this.repaint();
        dhtCallBack.setValue("DHT", t * 0.1, hummidity * 0.1, dataCtr, errCtr);
    }

    byte[] data;
    int t;
    int hummidity;
    int dataCtr;
    int errCtr;

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        dbg.println(DbgConfig.dbgLevelMaskGui | 9, "IotGaugeDht11 - paintComponent " + (Object)this);
        //java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
        int diagHeight = getHeight();
        int diagWidth = getWidth();
        Font fontDefault = g.getFont();
        g.setColor(new Color(255, 70, 0));
        g.fillRect(0, 0, diagWidth, diagHeight);
        g.setColor(Color.BLACK);
        g.drawRoundRect(0, 0, diagWidth - 1, diagHeight - 1, 15, 15);
        //g.drawRect(0, 0, diagWidth, diagHeight);
        g.setColor(Color.BLUE);
        int fontSize = ((diagWidth < diagHeight) ? diagWidth : diagHeight) / 2;
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        String valStr;
        if (data == null) {
            valStr = "NaN      ";
        }else
        if (data.length != 6) {
            valStr = "NaN (" + data.length + ")";
        }else{
            valStr = "" + (t / 10) + "." + (t % 10) + "C " + 
                          (hummidity / 10) + "." + (hummidity % 10) + "% " + dataCtr + " " + errCtr;
        }
        g.drawString(valStr, 30, diagHeight - 20);

        g.setColor(Color.BLACK);
        g.setFont(fontDefault);
        g.drawString("ctr=" + paintCtr, 30, 70);
        paintCtr++;
    }

    int toInt(byte b) {
        return (int)(b & 0xFF);
    }

    IotGaugeDhtChangeCallback dhtCallBack;
    int paintCtr = 0;

    private static final long serialVersionUID = 364126482051139014L;
}

class IotGaugeString extends IotGaugePanel {

    public IotGaugeString(String comPrefix) {
        setLogDataProcessorHandler(new IotDbg00Handler(comPrefix, this));
    }

    public void setValue(String val) {
        this.val = val;
        this.repaint();
        byte[] data = LogDataProcessorHexu8ArrayBase.toByteArray("DBG00", val);
        IotDataLogger.setData(0x20, data);
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        dbg.println(DbgConfig.dbgLevelMaskGui | 9, "IotGaugeString - paintComponent " + (Object)this);
        //java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
        int diagHeight = getHeight();
        int diagWidth = getWidth();
        Font fontDefault = g.getFont();
        g.setColor(new Color(255, 70, 0));
        g.fillRect(0, 0, diagWidth, diagHeight);
        g.setColor(Color.BLACK);
        g.drawRoundRect(0, 0, diagWidth - 1, diagHeight - 1, 15, 15);
        //g.drawRect(0, 0, diagWidth, diagHeight);
        g.setColor(Color.BLUE);
        int fontSize = ((diagWidth < diagHeight) ? diagWidth : diagHeight) / 2;
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g.drawString(val, 30, diagHeight - 20);

        g.setColor(Color.BLACK);
        g.setFont(fontDefault);
        g.drawString("ctr=" + paintCtr, 30, 70);
        paintCtr++;
    }

    protected String val = "-";
    int paintCtr = 0;

    private static final long serialVersionUID = 364126486431139014L;
}

class IotDataPanel extends JPanel {
    IotDataPanel(IotTerminalMainPanel _parent) {
        parent = _parent;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        addPanel(ibpr = new IotBatteryPanelResult());
        addPanel(ibp = new IotBatteryPanel(this));
        addPanel(new IotGaugeVoltage("U0", 3.3/4096, 0, ibp));
        addPanel(new IotGaugeVoltage("U1", 3.3/4096, 0, ibp));
        addPanel(new IotGaugeCurrent("I", 1, 0, ibp));
        addPanel(new IotGaugeDht11("DHT", new DhtCallBack()));
        addPanel(new IotGaugeString("DBG00"));
    }

    IotBatteryPanel ibp;

    void addPanel(Object child)
    {
        try {
            IotGaugePanel igp = (IotGaugePanel)child;
            panels.add(igp);
        }catch (Exception e)
        {
            dbg.println(19, "IotDataPanel.addPanel exception e=" + e.toString());
        }
        add((JComponent)child);
    }

    public IotGaugePanel getPanel(int idx) {
        if (idx < panels.size())
            return panels.get(idx);
        return null;
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        dbg.println(DbgConfig.dbgLevelMaskGui | 9, "IotDataPanel - paintComponent");
        //java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
        int diagHeight = getHeight();
        g.setColor(new Color(255, 70, 0));
        g.fillRect(0, 0, getWidth(), diagHeight);
        g.setColor(new Color(0, 0, 0));
        g.drawString("AbCD", 30, 70);
        g.setFont(new Font("Arial", Font.PLAIN, 40));
        g.drawString("BaCD", 30, 170);
    }

    public void setMeasData(double _u0, double _r) {
        u0 = _u0;
        r = _r;
        ibpr.setMeasData(u0, r);
    }

    double r = Double.NaN;
    double u0 = Double.NaN;

    IotTerminalMainPanel parent;
    Vector<IotGaugePanel> panels = new Vector<>();
    private IotBatteryPanelResult ibpr;

    private static final long serialVersionUID = 960859627532168948L;
}

class IotTerminalCommandEditor extends JPanel {
    IotTerminalCommandEditor(IotTerminalMainPanel parent) {
        this.parent = parent;
        setLayout();
    }

    void setLayout() {
        JLabel jl = new JLabel("Command to device:");
        signalNameFilterText = new JTextField();
        signalNameFilterText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                dbg.println(11, "signalNameFilterText.insertUpdate=" + signalNameFilterText.getText());
                //signalVisibilityFilterIsChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                dbg.println(11, "signalNameFilterText.removeUpdate=" + signalNameFilterText.getText());
                //signalVisibilityFilterIsChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                dbg.println(11, "signalNameFilterText.changedUpdate=" + signalNameFilterText.getText());
                //signalVisibilityFilterIsChanged();
            }
        });
        signalNameFilterText.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                dbg.println(11, "signalNameFilterText.actionPerformed=" + signalNameFilterText.getText() + " e=" + e.toString());
                parent.sendIotCommand(signalNameFilterText.getText() + '\r');
            }
        });
        //Container cp = getContentPane();
        SpringLayout layout = new SpringLayout();
        setLayout(layout);
        add(jl);
        add(signalNameFilterText);
        layout.putConstraint(SpringLayout.NORTH, jl, 10, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST,  jl, 5, SpringLayout.WEST,  this);

        layout.putConstraint(SpringLayout.NORTH, signalNameFilterText, -4, SpringLayout.NORTH, jl);
        layout.putConstraint(SpringLayout.WEST,  signalNameFilterText, 5, SpringLayout.EAST,  jl);
        layout.putConstraint(SpringLayout.EAST,  signalNameFilterText, -5, SpringLayout.EAST,  this);
        //pack();
        this.setMinimumSize(new Dimension(200, 50));
    }

    IotTerminalMainPanel parent;
    JTextField signalNameFilterText;
    private static final long serialVersionUID = -3090462396850956564L;
}

public class IotTerminalMainPanel extends JPanel {

    public IotTerminalMainPanel(IotTerminalMain _parent) {
        super(new BorderLayout());

        parent = _parent;

        upper = new IotDataPanel(this);
        JPanel bottom = new IotTerminalCommandEditor(this);
        logger = new JTextArea();
        logger.setEditable(false);
        horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, upper, new JScrollPane(logger));
        verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplit, bottom);
        horizontalSplit.setDividerLocation(IotTerminalPrefs.get("HorizontalSplit", 100));
        verticalSplit.setDividerLocation(IotTerminalPrefs.get("VerticalSplit", 100));
        add(verticalSplit);
        this.setMinimumSize(new Dimension(400, 300));
    }

    public void sendIotCommand(String cmd) {
        parent.sendIotCommand(cmd);
    }

    public void addLog(String msg) {
        if (msg.charAt(msg.length() - 1) != '\n')
            msg = msg + "\n";
        logger.append(msg);
    }

    public void saveWindowLayout() {
        dbg.println(9, "IotTerminalMainPanel.saveWindowLayout");
        IotTerminalPrefs.put("VerticalSplit", verticalSplit.getDividerLocation());
        IotTerminalPrefs.put("HorizontalSplit", horizontalSplit.getDividerLocation());
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        dbg.println(DbgConfig.dbgLevelMaskGui | 9, "IotTerminalMainPanel - paintComponent");
        //java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
        int diagHeight = getHeight();
        g.setColor(new Color(255, 100, 0));
        g.fillRect(0, 0, getWidth(), diagHeight);
    }

    public IotGaugePanel getPanel(int idx) {
        return upper.getPanel(idx);
    }

    private IotTerminalMain parent;
    JSplitPane verticalSplit;
    JSplitPane horizontalSplit;
    JTextArea logger;
    IotDataPanel upper;

    private static final long serialVersionUID = 2561142532984581795L;
}
