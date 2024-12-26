package IotTerminal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
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

class IotGaugeVoltage extends IotGaugePanel implements ActionListener {
    public IotGaugeVoltage(String comPrefix, double _factor, double _offset)
    {
        setLogDataProcessorHandler(new IotVoltageHandler(comPrefix, 3.3/4096, 0, this));
        t = new Timer(1000, this); // timeout in ms
        t.setRepeats(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setValue(Double.NaN);
    }

    public void setValue(double val) {
        if ((val > 7) || (val < 0))
            this.val = 0;
        else
            this.val = val;
        this.repaint();
        if (Double.isNaN(this.val))
            t.stop();
        else
            t.restart();
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

    private static final long serialVersionUID = 364126482051139014L;
}

class IotGaugeCurrent extends IotGaugeVoltage {
    public IotGaugeCurrent(String comPrefix, double factor, double offset) {
        super(comPrefix, factor, offset);
    }

    @Override
    public void setValue(double val) {
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

class IotGaugeDht11 extends IotGaugePanel {
    public IotGaugeDht11(String _comPrefix) {
        setLogDataProcessorHandler(new IotDht11Handler("DHT", this));
    }

    public void setValue(byte[] data) {
        this.data = data;
        this.repaint();
    }

    byte[] data;

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
            int t         = toInt(data[0]) * 256 + toInt(data[1]);
            int hummidity = toInt(data[2]) * 256 + toInt(data[3]);
            int dataCtr   = toInt(data[4]);
            int errCtr    = toInt(data[5]);
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
    protected double val = Double.NaN;
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

class IotBatteryPanel extends JPanel
{
    IotBatteryPanel()
    {
        super(new BorderLayout());

        Dimension d = new Dimension(2000, 100);

        data = new JPanel();

        logger = new JTextArea();

        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, data, new JScrollPane(logger));

        add(horizontalSplit);
        this.setMaximumSize(d);
    }

    JPanel data;
    JTextArea logger;

    private static final long serialVersionUID = -1492376671650452880L;
}

class IotDataPanel extends JPanel {
    IotDataPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        addPanel(ibp = new IotBatteryPanel());
        addPanel(new IotGaugeVoltage("U0", 3.3/4096, 0));
        addPanel(new IotGaugeVoltage("U1", 3.3/4096, 0));
        addPanel(new IotGaugeCurrent("I", 1, 0));
        addPanel(new IotGaugeDht11("DHT"));
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
        add((JPanel)child);
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

    Vector<IotGaugePanel> panels = new Vector<>();

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

        upper = new IotDataPanel();
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
