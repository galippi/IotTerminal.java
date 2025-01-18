package IotTerminal;

import java.awt.Font;

import javax.swing.JLabel;

public class IotBatteryPanelResult extends JLabel {
    IotBatteryPanelResult() {
        super();
        final int fontSize = 32;
        setFont(new Font("Arial", Font.PLAIN, fontSize));
        setHorizontalAlignment(JLabel.LEFT);
        setText("No");
    }

    public void setMeasData(double u0, double r, double mAh) {
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
        msg = msg + " capacity= ";
        msg = msg + String.format("%.3f", mAh) + " mAh";
        this.setText(msg);
    }

    private static final long serialVersionUID = 9181881569404540886L;
}
