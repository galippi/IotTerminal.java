package IotTerminal;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lippiWare.utils.dbg;

public class IotTerminalMainPanel extends JPanel {

    public IotTerminalMainPanel(JFrame _parent) {
        parent = _parent;
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
    private static final long serialVersionUID = 2561142532984581795L;
}
