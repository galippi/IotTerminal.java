package IotTerminal;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;

import iotDriver.IotActivatedDriverList;
import iotDriver.IotAvailableDriverList;
import iotDriver.IotDriverBase;

class IotAvailableDriversTable extends JTable {

    IotAvailableDriversTable() {
        //super(2, /*2, columnNames.length*/ 2);
        setModel( new javax.swing.table.DefaultTableModel(columnNames, IotAvailableDriverList.size()));

        javax.swing.table.TableColumnModel columnModel = getColumnModel();
        for (int i = 0; i < columnNames.length; i++)
        {
            //TableColumn column = new TableColumn();
            //columnModel.addColumn(column);
            TableColumn column = columnModel.getColumn(i);
            column.setMinWidth(50);
            column.setMaxWidth(1000);
            column.setWidth(50);
            column.setResizable(true);
            column.setHeaderValue(columnNames[i]);
        }

        for(int i = 0; i < IotAvailableDriverList.size(); i++) {
            IotDriverBase driver = IotAvailableDriverList.get(i);
            setValueAt(driver.getName(), i, colName);
            setValueAt("" + driver.getSubchannelNumber(), i, colChannelsNumber);
        }
    }

    final String[] columnNames = new String[] {
            "Device name", "Number os supported channels"
        };
    final static int colName = 0;
    final static int colChannelsNumber = 1;
    private static final long serialVersionUID = -6460641380023649803L;
}

public class IotActivateDriver extends JDialog {
    IotActivateDriver(IotDeviceSetupDlg _parent) {
        super(_parent, Dialog.ModalityType.APPLICATION_MODAL);
        parent = _parent;
        this.setTitle("Activate device");
        setMinimumSize(new Dimension(350, 200));
        //setLayout(new BorderLayout());
        setLayout(new FlowLayout());
        //setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        table = new IotAvailableDriversTable();
        JScrollPane scrollableTable = new JScrollPane(table);

        JButton bAdd = new JButton("Add");
        bAdd.setHorizontalAlignment(SwingConstants.LEFT);
        bAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addHandler();
            }
        });

        JButton bCancel = new JButton("Cancel");
        bCancel.setHorizontalAlignment(SwingConstants.RIGHT);
        bCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        JPanel bAddCancel = new JPanel();
        bAddCancel.add(bAdd);
        bAddCancel.add(bCancel);

        add(scrollableTable);
        add(bAddCancel);

        pack();

        addEscapeListener();
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

    private void addHandler() { // TODO:
        setVisible(false);
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length > 0) {
            for (int i = 0; i < selectedRows.length; i++) {
                IotActivatedDriverList.add(IotAvailableDriverList.get(selectedRows[i]).create());
            }
            parent.updateDeviceList();
        }
    }

    IotDeviceSetupDlg parent;
    private IotAvailableDriversTable table;

    private static final long serialVersionUID = 2131385812160522912L;
}
