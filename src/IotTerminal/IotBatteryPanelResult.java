package IotTerminal;

import javax.swing.JLabel;

public class IotBatteryPanelResult extends JLabel {
    IotBatteryPanelResult() {
        super();
        setText("No");
    }

    public void setMeasData(double u0, double r) {
        String msg = "Result: R= ";
        if (Double.isNaN(r))
            msg = msg + "NaN";
        else
            msg = msg + String.format("%.1f", r * 1000) + " mOhm";
        msg = msg + " U0= ";
        if (Double.isNaN(u0))
            msg = msg + "NaN";
        else
            msg = msg + String.format("%.3f", u0) + " V";
        this.setText(msg);
    }

    private static final long serialVersionUID = 9181881569404540886L;
}
