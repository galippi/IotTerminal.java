package IotTerminal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import config.DbgConfig;
import lippiWare.utils.dbg;

class IotBatteryDataPanel extends JPanel
{
    IotBatteryDataPanel(IotBatteryPanel _parent)
    {
        parent = _parent;
    }

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
        String val = "ctr=" + paintCtr + " " + IotBatteryStateMachine.toString(parent.state) + " " + parent.u0 + "V " +
                parent.ut + "V " + parent.i + "mA " + (parent.r * 1000) + " mOhm";
        g.drawString(val, 10, 15);
        paintCtr++;
    }

    IotBatteryPanel parent;
    int paintCtr;

    private static final long serialVersionUID = 584471610212471627L;
}

enum IotBatteryStateMachine
{
    IBSM_init,
    IBSM_meas_u0,
    IBSM_set_It,
    IBSM_meas_u1,
    IBSM_meas_ready,
    IBSM_meas_timeout,
    IBSM_meas_duty_error,
    IBSM_meas_error;

    static String toString(IotBatteryStateMachine state)
    {
        switch(state)
        {
            case IBSM_init:
                return "IBSM_init";
            case IBSM_meas_u0:
                return "IBSM_meas_u0";
            case IBSM_set_It:
                return "IBSM_set_It";
            case IBSM_meas_u1:
                return "IBSM_meas_u1";
            case IBSM_meas_ready:
                return "IBSM_meas_ready";
            case IBSM_meas_timeout:
                return "IBSM_meas_timeout";
            case IBSM_meas_duty_error:
                return "IBSM_meas_duty_error";
            case IBSM_meas_error:
                return "IBSM_meas_error";
            default:
                return "IotBatteryStateMachine - error";
        }
    }
}

class IotBatteryPanel extends JPanel implements IotGaugeValueChangeCallback, ActionListener
{
    private int duty;
    private IotDataPanel parent;

    IotBatteryPanel(IotDataPanel _parent)
    {
        super(new BorderLayout());
        parent = _parent;

        Dimension d = new Dimension(2000, 200);

        data = new IotBatteryDataPanel(this);
        data.setMinimumSize(new Dimension(300, 200));

        logger = new JTextArea();
        logger.setMinimumSize(new Dimension(100, 200));

        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, data, new JScrollPane(logger));

        add(horizontalSplit);
        //this.setMinimumSize(d);
        this.setMaximumSize(d);
        t = new Timer(200, this);
        t.setRepeats(true);
    }

    void setU(double newVal)
    {
        u = newVal;
        tu = System.nanoTime();
        stateMachineUpdate();
    }

    void setI(double newVal)
    {
        i = newVal;
        ti = System.nanoTime();
        stateMachineUpdate();
    }

    void stateMachineUpdate()
    {
        long t = System.nanoTime();
        long dt = t - stateTimer;
        if ((tu - t)> t_1_sec_in_ns)
            u = Double.NaN;
        if ((ti - t)> t_1_sec_in_ns)
            i = Double.NaN;
        if ((Double.isNaN(u)) || (Double.isNaN(i)) || (u < 0.1)) {
            state = IotBatteryStateMachine.IBSM_init;
            r = Double.NaN;
        }
        else {
            switch(state)
            {
                case IBSM_init:
                    r = Double.NaN;
                    if ((u > 1.1) && (i < 10))
                    {
                        state = IotBatteryStateMachine.IBSM_meas_u0;
                        stateTimer = t;
                    }
                    break;
                case IBSM_meas_u0:
                    if ((u > 1.1) && (i < 10) && (dt > 1_000_000_000))
                    {
                        state = IotBatteryStateMachine.IBSM_meas_u1;
                        u0 = u;
                        duty = 0x50;
                        sendDutyRequest(duty);
                        stateTimer = t;
                    }
                    break;
                case IBSM_meas_u1:
                    if (dt > t_5_sec_in_ns)
                    {
                        state = IotBatteryStateMachine.IBSM_meas_timeout;
                    }else
                    {
                        if (u < 0.9) {
                            state = IotBatteryStateMachine.IBSM_meas_error;
                        }else if (i < 120) {
                            duty = duty + 4;
                            if (duty > 240) {
                                state = IotBatteryStateMachine.IBSM_meas_duty_error;
                                duty = 240;
                            }
                            else
                                sendDutyRequest(duty);
                        }else {
                            ut = u;
                            it = i;
                            if (dt > t_2_sec_in_ns) {
                                r = (u0 - ut) * 1000 / it;
                                state = IotBatteryStateMachine.IBSM_meas_ready;
                            }
                        }
                    }
                    break;
                case IBSM_meas_ready:
                    break;
                default:
                    //state = IotBatteryStateMachine.IBSM_init;
                    break;
            }
        }
        data.repaint();
    }

    private void sendDutyRequest(int _duty) {
        parent.parent.sendIotCommand("SR" + Integer.toHexString(_duty) + '\r');
//        parent.sendIotCommand("SR" + Integer.toHexString(_duty) + '\r');
    }

    @Override
    public void setValue(String type, double val) {
        if (type.contentEquals("U0"))
            setU(val);
        else
        if (type.contentEquals("I"))
            setI(val);
        else
            ; // do nothing
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        stateMachineUpdate();
    }

    private final long t_1_sec_in_ns = 1_000_000_000;
    private final long t_2_sec_in_ns = 2 * t_1_sec_in_ns;
    private final long t_5_sec_in_ns = 5 * t_1_sec_in_ns;

    IotBatteryDataPanel data;
    JTextArea logger;
    Timer t;
    double u, u0, ut;
    long tu;
    double i, it;
    long ti;
    double r = Double.NaN;
    IotBatteryStateMachine state = IotBatteryStateMachine.IBSM_init;
    long stateTimer;

    private static final long serialVersionUID = -1492376671650452880L;
}
