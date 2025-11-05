package IotTerminal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import lippiWare.utils.dbg;

class IotDeviceTable extends JPanel {
    IotDeviceTable() {
        super(new BorderLayout());
        JPanel myTable = new JPanel();
        JScrollPane scrollableTable = new JScrollPane(myTable);
        scrollableTable.setMinimumSize(new Dimension(350, 200));
        add(scrollableTable);
    }

    private static final long serialVersionUID = 7799538399508125604L;
}

public class IotDeviceSetupDlg extends JDialog {

    IotDeviceSetupDlg(IotTerminalMain _parent) {
        parent = _parent;
        this.setTitle("Device setup");

        IotDeviceTable scrollableTable = new IotDeviceTable();

        JPanel channelsPanel = new JPanel();
        //channelsPanel.setMinimumSize(new Dimension(350, 200));
        JScrollPane channelsPane = new JScrollPane(channelsPanel);
        channelsPane.setMinimumSize(new Dimension(350, 200));

        jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollableTable , channelsPane);
        jsp.setDividerLocation(IotTerminalPrefs.get("DeviceSetupVerticalSplit", 200));
        JPanel horizontalSplit = new JPanel(new BorderLayout());
        horizontalSplit.add(jsp);
        horizontalSplit.setMinimumSize(new Dimension(350, 400));

        JButton bOk = new JButton("OK");
        bOk.setHorizontalAlignment(SwingConstants.LEFT);
        bOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okHandler();
            }
        });

        JButton bCancel = new JButton("Cancel");
        bCancel.setHorizontalAlignment(SwingConstants.RIGHT);
        bCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        JPanel bOkCancel = new JPanel();
        bOkCancel.add(bOk);
        bOkCancel.add(bCancel);

        Container cp = getContentPane();
        SpringLayout layout = new SpringLayout();
        cp.setLayout(layout);

        cp.add(horizontalSplit);
        cp.add(bOkCancel);

        layout.putConstraint(SpringLayout.WEST,  horizontalSplit,  5, SpringLayout.WEST,  cp);
        layout.putConstraint(SpringLayout.EAST,  horizontalSplit, -5, SpringLayout.EAST,  cp);
        layout.putConstraint(SpringLayout.NORTH, horizontalSplit,  5, SpringLayout.NORTH, cp);
        layout.putConstraint(SpringLayout.SOUTH, horizontalSplit, -5, SpringLayout.NORTH, bOkCancel);

        layout.putConstraint(SpringLayout.NORTH,             bOkCancel, -40, SpringLayout.SOUTH,             cp);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, bOkCancel,   0, SpringLayout.HORIZONTAL_CENTER, cp);
        layout.putConstraint(SpringLayout.SOUTH,             bOkCancel,  -5, SpringLayout.SOUTH,             cp);

        pack();

        Point pt = parent.getLocationOnScreen();
        int pw = parent.getWidth();
        setBounds(pt.x + pw / 2 - 150, pt.y + 200, 400, 400);
        setLocation(IotTerminalPrefs.get("DeviceSetupDialogX", 0), IotTerminalPrefs.get("DeviceSetupDialogY", 0));
        setSize(IotTerminalPrefs.get("DeviceSetupDialogW", 350), IotTerminalPrefs.get("DeviceSetupDialogH", 300));
        this.setMinimumSize(new Dimension(350, 400));

        addEscapeListener();
        fillRowData();
        updateButtons();

    }

    void addEscapeListener() {
        ActionListener escListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };
        getRootPane().registerKeyboardAction(escListener,
              KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
              JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void fillRowData() {
        // TODO Auto-generated method stub
        
    }

    private void updateButtons() {
        // TODO Auto-generated method stub
        
    }

    protected void okHandler() {
        dbg.println(9, "ChannelSelectorDialog.okHandler");
        setVisible(false);

        IotTerminalPrefs.put("DeviceSetupDialogX", getX());
        IotTerminalPrefs.put("DeviceSetupDialogY", getY());
        IotTerminalPrefs.put("DeviceSetupDialogH", getHeight());
        IotTerminalPrefs.put("DeviceSetupDialogW", getWidth());
        IotTerminalPrefs.put("DeviceSetupVerticalSplit", jsp.getDividerLocation());
    }

    IotTerminalMain parent;
    JSplitPane jsp;
    private static final long serialVersionUID = 8080722709719513891L;
}
