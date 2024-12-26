package IotTerminal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import config.DbgConfig;
import lippiWare.utils.dbg;

class IotBatteryDataPanel extends JPanel
{
    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        dbg.println(DbgConfig.dbgLevelMaskGui | 9, "IotGaugeString - paintComponent " + (Object)this);
        //java.awt.Graphics2D g2 = (java.awt.Graphics2D)g;
        int diagHeight = getHeight();
        int diagWidth = getWidth();
        Font fontDefault = g.getFont();
        g.setColor(new Color(155, 120, 0));
        g.fillRect(0, 0, diagWidth, diagHeight);
        g.setColor(Color.BLACK);
        g.drawRoundRect(0, 0, diagWidth - 1, diagHeight - 1, 15, 15);
        //g.drawRect(0, 0, diagWidth, diagHeight);
        g.setColor(Color.BLUE);
        int fontSize = ((diagWidth < diagHeight) ? diagWidth : diagHeight) / 2;
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
//        g.drawString(val, 30, diagHeight - 20);

        g.setColor(Color.BLACK);
        g.setFont(fontDefault);
        g.drawString("ctr=" + paintCtr, 30, 70);
        paintCtr++;
    }

    int paintCtr;

    private static final long serialVersionUID = 584471610212471627L;
}

class IotBatteryPanel extends JPanel implements IotGaugeValueChangeCallback
{
    IotBatteryPanel()
    {
        super(new BorderLayout());

        Dimension d = new Dimension(2000, 200);

        data = new IotBatteryDataPanel();
        data.setMinimumSize(new Dimension(300, 200));

        logger = new JTextArea();
        logger.setMinimumSize(new Dimension(100, 200));

        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, data, new JScrollPane(logger));

        add(horizontalSplit);
        //this.setMinimumSize(d);
        this.setMaximumSize(d);
    }

    @Override
    public void setValue(String type, double val) {
        if (type.contentEquals("U0"))
            u0 = val;
        else
        if (type.contentEquals("I"))
            i = val;
        else
            ; // do nothing
    }

    IotBatteryDataPanel data;
    JTextArea logger;
    double u0;
    double i;

    private static final long serialVersionUID = -1492376671650452880L;
}
