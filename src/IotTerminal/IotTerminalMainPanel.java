package IotTerminal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import lippiWare.utils.dbg;

class IotDataPanel extends JPanel {
    IotDataPanel() {
        
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        dbg.println(9, "IotDataPanel - paintComponent");
        java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
        int diagHeight = getHeight();
        g.setColor(new Color(255, 70, 0));
        g.fillRect(0, 0, getWidth(), diagHeight);
        g.setColor(new Color(0, 0, 0));
        g.drawString("AbCD", 30, 70);
        g.setFont(new Font("Arial", Font.PLAIN, 40));
        g.drawString("BaCD", 30, 170);
    }

    private static final long serialVersionUID = 960859627532168948L;
}

public class IotTerminalMainPanel extends JPanel {

    public IotTerminalMainPanel(JFrame _parent) {
        super(new BorderLayout());

        parent = _parent;

        JPanel upper = new IotDataPanel();
        JPanel bottom = new JPanel();
        logger = new JTextArea();
        horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, upper, logger);
        verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplit, bottom);
        horizontalSplit.setDividerLocation(IotTerminalPrefs.get("HorizontalSplit", 100));
        verticalSplit.setDividerLocation(IotTerminalPrefs.get("VerticalSplit", 100));
        add(verticalSplit);
    }
    JTextArea logger;

    public void addLog(String msg) {
        logger.append(msg + "\n");
    }

    public void saveWindowLayout() {
        dbg.println(9, "IotTerminalMainPanel.saveWindowLayout");
        IotTerminalPrefs.put("VerticalSplit", verticalSplit.getDividerLocation());
        IotTerminalPrefs.put("HorizontalSplit", horizontalSplit.getDividerLocation());
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        dbg.println(9, "IotTerminalMainPanel - paintComponent");
        java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
        int diagHeight = getHeight();
        g.setColor(new Color(255, 100, 0));
        g.fillRect(0, 0, getWidth(), diagHeight);
    }

    private JFrame parent;
    JSplitPane verticalSplit;
    JSplitPane horizontalSplit;
    private static final long serialVersionUID = 2561142532984581795L;
}
