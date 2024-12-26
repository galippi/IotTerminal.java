package IotTerminal;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

class IotBatteryPanel extends JPanel
{
    IotBatteryPanel()
    {
        super(new BorderLayout());

        Dimension d = new Dimension(2000, 200);

        data = new JPanel();
        data.setMinimumSize(new Dimension(300, 200));

        logger = new JTextArea();
        logger.setMinimumSize(new Dimension(100, 200));

        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, data, new JScrollPane(logger));

        add(horizontalSplit);
        //this.setMinimumSize(d);
        this.setMaximumSize(d);
    }

    JPanel data;
    JTextArea logger;

    private static final long serialVersionUID = -1492376671650452880L;
}
