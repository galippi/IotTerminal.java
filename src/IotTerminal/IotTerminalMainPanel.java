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
import lippiWare.utils.dbg;
import lwLogDataProcessor.LogDataProcessorHandlerBase;
import lwLogDataProcessor.LogDataProcessorHexu8ArrayBase;

class IotDataPanel extends JPanel {
    IotDataPanel(IotTerminalMainPanel _parent) {
        parent = _parent;
    }

    IotTerminalMainPanel parent;

    private static final long serialVersionUID = 3618667313067833158L;
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
        //parent.sendIotCommand(cmd);
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

    private IotTerminalMain parent;
    JSplitPane verticalSplit;
    JSplitPane horizontalSplit;
    JTextArea logger;
    IotDataPanel upper;

    private static final long serialVersionUID = 2561142532984581795L;
}
