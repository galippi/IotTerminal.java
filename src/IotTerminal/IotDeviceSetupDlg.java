package IotTerminal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import iotDriver.IotActivatedDriverList;
import iotDriver.IotDriverBase;
import iotDriver.IotDriverBaseConfigDlg;
import lippiWare.utils.dbg;

class IotDeviceTable extends JPanel {
    IotDeviceTable() {
        super(new BorderLayout());
        table = new JTable(2, columnNames.length);
        table.setModel( new javax.swing.table.DefaultTableModel(columnNames, IotActivatedDriverList.size()));
        javax.swing.table.TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnNames.length; i++)
        {
            TableColumn column = columnModel.getColumn(i);
            column.setMinWidth(50);
            column.setMaxWidth(1000);
            column.setWidth(50);
            column.setResizable(true);
            column.setHeaderValue(columnNames[i]);
        }

        fillRowData();

        JScrollPane scrollableTable = new JScrollPane(table);
        scrollableTable.setMinimumSize(new Dimension(350, 200));
        add(scrollableTable);
    }

    public void fillRowData() {
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.setRowCount(IotActivatedDriverList.size());
        for(int i = 0; i < IotActivatedDriverList.size(); i++) {
            IotDriverBase driver = IotActivatedDriverList.get(i);
            table.setValueAt(driver.getName(), i, colName);
            table.setValueAt("" + driver.getSubchannelNumber(), i, colChNum);
        }
    }

    JTable table;

    static final String[] columnNames = new String[] {
            "Device name", "Number of configured channels"
        };
    static final int colName = 0;
    static final int colChNum = 1;

    private static final long serialVersionUID = 7799538399508125604L;
}

class IotChannelsTable extends JPanel {
    IotChannelsTable() {
        super(new BorderLayout());
        JTable table = new JTable(2, columnNames.length);
        javax.swing.table.TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnNames.length; i++)
        {
            TableColumn column = columnModel.getColumn(i);
            column.setMinWidth(50);
            column.setMaxWidth(1000);
            column.setWidth(50);
            column.setResizable(true);
            column.setHeaderValue(columnNames[i]);
        }

        JScrollPane scrollableTable = new JScrollPane(table);
        scrollableTable.setMinimumSize(new Dimension(350, 200));
        add(scrollableTable);
    }

    final String[] columnNames = new String[] {
            "Channel name", "Channel index", "Device name", "Device index", "Channel sub index"
        };

    private static final long serialVersionUID = 7799538399508125604L;
}

public class IotDeviceSetupDlg extends JDialog {
    IotDeviceSetupDlg(IotTerminalMain _parent) {
        super(_parent, Dialog.ModalityType.APPLICATION_MODAL);
        parent = _parent;
        this.setTitle("Device setup");
        idsd = this;

        devicesActivated = new IotDeviceTable();
        devicesActivated.table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener (){
                    @Override
                    public void valueChanged(ListSelectionEvent evt)
                    {
                        dbg.println(9, "IotDeviceSetupDlg.ListSelectionListener evt=" + evt.toString());
                        if (!evt.getValueIsAdjusting())
                            updateButtons();
                    }
            });


        IotChannelsTable channelsPanel = new IotChannelsTable();
        //channelsPanel.setMinimumSize(new Dimension(350, 200));
        JScrollPane channelsPane = new JScrollPane(channelsPanel);
        channelsPane.setMinimumSize(new Dimension(350, 200));

        jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, devicesActivated , channelsPane);
        jsp.setDividerLocation(IotTerminalPrefs.get("DeviceSetupVerticalSplit", 200));
        JPanel horizontalSplit = new JPanel(new BorderLayout());
        horizontalSplit.add(jsp);
        horizontalSplit.setMinimumSize(new Dimension(350, 400));

        JPanel buttons = new JPanel();

        JButton bAdd = new JButton("Add");
        bAdd.setHorizontalAlignment(SwingConstants.LEFT);
        bAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addHandler();
            }
        });

        bRemove = new JButton("Remove");
        bRemove.setHorizontalAlignment(SwingConstants.LEFT);
        bRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dbg.println(9, "IotDeviceSetupDlg.Remove.actionPerformed e=" + e.toString());
            }
        });

        bConfigure = new JButton("Configure");
        bConfigure.setHorizontalAlignment(SwingConstants.LEFT);
        bConfigure.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dbg.println(9, "IotDeviceSetupDlg.Configure.actionPerformed e=" + e.toString());
                configureHandler();
            }
        });

        bCheck = new JButton("Check config");
        bCheck.setHorizontalAlignment(SwingConstants.LEFT);
        bCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dbg.println(9, "IotDeviceSetupDlg.Check config.actionPerformed e=" + e.toString());
                checkDevice();
            }
        });

        buttons.add(bAdd);
        buttons.add(bRemove);
        buttons.add(bConfigure);
        buttons.add(bCheck);

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
                dbg.println(9, "IotDeviceSetupDlg.Cancel.actionPerformed e=" + e.toString());
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
        cp.add(buttons);
        cp.add(bOkCancel);

        layout.putConstraint(SpringLayout.WEST,  buttons, -95, SpringLayout.EAST,  cp);
        layout.putConstraint(SpringLayout.EAST,  buttons,  -5, SpringLayout.EAST,  cp);
        layout.putConstraint(SpringLayout.NORTH, buttons,  5, SpringLayout.NORTH, cp);
        layout.putConstraint(SpringLayout.SOUTH, buttons, -5, SpringLayout.NORTH, bOkCancel);

        layout.putConstraint(SpringLayout.WEST,  horizontalSplit,  5, SpringLayout.WEST,  cp);
        layout.putConstraint(SpringLayout.EAST,  horizontalSplit, -5, SpringLayout.WEST,  buttons);
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
        if (devicesActivated.table.getRowCount() > 0)
            devicesActivated.table.setRowSelectionInterval(0, 0);
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
        devicesActivated.fillRowData();
    }

    private void updateButtons() {
        dbg.println(9, "IotDeviceSetupDlg.updateButtons");
        int[] rowIdxs = devicesActivated.table.getSelectedRows();
        if (rowIdxs.length == 1) {
            bRemove.setEnabled(true);
            if (IotActivatedDriverList.get(rowIdxs[0]).getConfigDlg() != null)
                bConfigure.setEnabled(true);
            else
                bConfigure.setEnabled(false);
            bCheck.setEnabled(true);
        }else
        if (rowIdxs.length > 0) {
            bRemove.setEnabled(true);
            bConfigure.setEnabled(false);
            bCheck.setEnabled(false);
        }else
        {
            bRemove.setEnabled(false);
            bConfigure.setEnabled(false);
            bCheck.setEnabled(false);
        }
    }

    protected void okHandler() {
        dbg.println(9, "IotDeviceSetupDlg.okHandler");
        setVisible(false);

        IotTerminalPrefs.put("DeviceSetupDialogX", getX());
        IotTerminalPrefs.put("DeviceSetupDialogY", getY());
        IotTerminalPrefs.put("DeviceSetupDialogH", getHeight());
        IotTerminalPrefs.put("DeviceSetupDialogW", getWidth());
        IotTerminalPrefs.put("DeviceSetupVerticalSplit", jsp.getDividerLocation());
    }

    protected void addHandler() {
        dbg.println(9, "IotDeviceSetupDlg.addHandler");
        IotActivateDriver iad = new IotActivateDriver(this);
        iad.setVisible(true);
    }

    public void updateDeviceList() {
        //fillRowData();
        devicesActivated.fillRowData();
    }

    private void configureHandler() {
        dbg.println(9, "IotDeviceSetupDlg.configureHandler");
        int[] rowIdxs = devicesActivated.table.getSelectedRows();
        if (rowIdxs.length == 1) {
            IotDriverBaseConfigDlg dlg = IotActivatedDriverList.get(rowIdxs[0]).getConfigDlg();
            if (dlg != null) {
                dlg.setVisible(true);
            }
        }
    }

    protected void checkDevice() {
        dbg.println(9, "IotDeviceSetupDlg.checkDevice");
        int[] rowIdxs = devicesActivated.table.getSelectedRows();
        if (rowIdxs.length != 1) {
            return; // not valid selection
        }
        IotDriverBase device = IotActivatedDriverList.get(rowIdxs[0]);
        String response = device.checkDevice();
        JOptionPane.showMessageDialog(parent, "Device response: " + response);
    }

    IotTerminalMain parent;
    JSplitPane jsp;
    private IotDeviceTable devicesActivated;
    private JButton bRemove;
    private JButton bConfigure;
    private JButton bCheck;

    public static IotDeviceSetupDlg idsd;

    private static final long serialVersionUID = 8080722709719513891L;
}
