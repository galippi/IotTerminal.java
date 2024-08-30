package IotTerminal;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import lippiWare.utils.dbg;

public class IotTerminalMainPanel extends JPanel {

    public IotTerminalMainPanel(JFrame _parent) {
        super(new BorderLayout());

        parent = _parent;

        JPanel upper = new JPanel();
        JPanel bottom = new JPanel();
        JPanel logger = new JPanel();
        horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, upper, logger);
        verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horizontalSplit, bottom);
        horizontalSplit.setDividerLocation(IotTerminalPrefs.get("HorizontalSplit", 100));
        verticalSplit.setDividerLocation(IotTerminalPrefs.get("VerticalSplit", 100));
        add(verticalSplit);
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
