/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IotTerminal;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import iotDataConnection.IotComPort;
import lippiWare.utils.dbg;

/**
 *
 * @author liptakok
 */

abstract class RowHandler
{
    abstract String getName();
    abstract Object getValue();
    abstract void setValue(Object newValue) throws Exception;
    abstract void update();

    int getId()
    {
        return id;
    }
    int id;
}

class RowHandlerDebugLevel extends RowHandler
{
    @Override
    String getName() {
        return "Debug level";
    }

    @Override
    Object getValue() {
        return "" + dbg.get();
    }

    @Override
    void setValue(Object newValue) throws Exception {
        newLevel = Integer.parseInt((String) newValue);
        if (newLevel < 0)
            throw new Exception("RowHandlerDebugLevel.setValue - the new level (" + newLevel + ") shall be > 0!");
    }

    @Override
    void update() {
        if (newLevel < 0)
            throw new Error("RowHandlerDebugLevel.update (newLevel=" + newLevel + ")!");
        IotTerminalPrefs.put("Debug level", newLevel);
        dbg.set(newLevel);
    }

    int newLevel = -1;
}

class RowHandlerPortName extends RowHandler
{
    @Override
    String getName() {
        return "Port name";
    }

    @Override
    Object getValue() {
        return IotComPort.getPortName();
    }

    @Override
    void setValue(Object newValue) throws Exception {
        comPortName = (String)newValue;
        if ((comPortName.length() < 1) || (!IotComPort.isComPortValid(comPortName)))
            throw new Exception("Invalid COM port name (" + comPortName + ")");
    }

    @Override
    void update() {
        IotComPort.openPort(comPortName);
    }

    String comPortName;
}

class RowHandlerBaudRate extends RowHandler
{
    @Override
    String getName() {
        return "Baud rate";
    }

    @Override
    Object getValue() {
        return ""+IotComPort.getBaudRate();
    }

    @Override
    void setValue(Object newValue) throws Exception {
        baudRate = Integer.parseInt((String)newValue);
        if ((baudRate < 100) || (baudRate > 10000000))
            throw new Exception("Invalid baud rate");
    }

    @Override
    void update() {
        IotComPort.setBaudRate(baudRate);
    }

    int baudRate;
}

class RowHandlerPollingTime extends RowHandler
{
    @Override
    String getName() {
        return "Polling time of COM port [ms]";
    }

    @Override
    Object getValue() {
        return ""+IotComPort.getPollingTime();
    }

    @Override
    void setValue(Object newValue) throws Exception {
        pollingTime = Integer.parseInt((String)newValue);
        if ((pollingTime < 10) || (pollingTime > 5000))
            throw new Exception("Invalid polling time");
    }

    @Override
    void update() {
        IotComPort.setPollingTime(pollingTime);
    }

    int pollingTime;
}

class OptionDialogRowListHandler
{
    RowHandler addRow(RowHandler row)
    {
        row.id = rows.size();
        rows.add(row);
        return row;
    }

    int getRowCount()
    {
        return rows.size();
    }

    RowHandler get(int idx)
    {
        return rows.get(idx);
    }

    Vector<RowHandler> rows = new Vector<>();
}

public class OptionsDialog extends JDialog {

  //headers for the table
  final String[] columnNames = new String[] {
      "Property name", "Property value"
  };
  final int colValue = 1;
  OptionDialogRowListHandler odrlh = new OptionDialogRowListHandler();

  IotTerminalMain parent;

  OptionsDialog(IotTerminalMain _parent)
  {
    super(_parent, Dialog.ModalityType.APPLICATION_MODAL);
    parent = _parent;
    this.setTitle("Options");

    odrlh.addRow(new RowHandlerDebugLevel());
    odrlh.addRow(new RowHandlerPortName());
    odrlh.addRow(new RowHandlerBaudRate());
    odrlh.addRow(new RowHandlerPollingTime());

    //create table with data
    table = new JTable(new DefaultTableModel(4, columnNames.length) {
        @Override
        public boolean isCellEditable(int row, int column)
        {
            return (column == 1);
        }
        private static final long serialVersionUID = 2641690847759012960L;
    });
    javax.swing.table.TableColumnModel columnModel = table.getColumnModel();
    for (int i = 0; i < columnNames.length; i++)
    {
        TableColumn column = columnModel.getColumn(i);
        column.setMinWidth(10);
        column.setMaxWidth(200);
        column.setWidth(10);
        column.setResizable(true);
        column.setHeaderValue(columnNames[i]);
    }

    for (int i = 0; i < odrlh.getRowCount(); i++)
    {
        RowHandler row = odrlh.get(i);
        table.setValueAt(row.getName() + ":", i, 0);
        table.setValueAt(row.getValue(), i, colValue);
    }

    table.addMouseListener(new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            mouseHandlerTable(e);
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            mouseHandlerTable(e);
        }

        @Override
        public void mouseReleased(MouseEvent e)
        { // not used - do nothing
        }

        @Override
        public void mouseEntered(MouseEvent e)
        { // not used - do nothing
        }

        @Override
        public void mouseExited(MouseEvent e)
        { // not used - do nothing
        }
    });
    table.getModel().addTableModelListener(
            new javax.swing.event.TableModelListener()
            {
                public void tableChanged(javax.swing.event.TableModelEvent evt) 
                {
                  tableChangedHandler(evt);
                }
    });

    //add the table to the frame
    this.add(new JScrollPane(table));

    JButton bOk = new JButton("Ok");
    //b2.setHorizontalAlignment(SwingConstants.CENTER);
    bOk.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            okHandler();
        }
    });
    JButton bCancel = new JButton("Cancel");
    //b2.setHorizontalAlignment(SwingConstants.CENTER);
    bCancel.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            cancelHandler();
        }
    });
    JPanel bOkCancel = new JPanel();
    bOkCancel.add(bOk);
    bOkCancel.add(bCancel);
    Container cp = getContentPane();
    // add label, text field and button one after another into a single column
    cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
    cp.add(bOkCancel, BorderLayout.SOUTH);

    //Point pt = parent.getLocationOnScreen();
    //int pw = parent.getWidth();
    //setBounds(pt.x + pw / 2 - 150, pt.y + 200, 400, 400);
    setLocation(IotTerminalPrefs.get("OptionsDialogX", 0), IotTerminalPrefs.get("OptionsDialogY", 0));
    setSize(IotTerminalPrefs.get("OptionsDialogW", 350), IotTerminalPrefs.get("OptionsDialogH", 300));
    this.setMinimumSize(new Dimension(350, 300));
    addEscapeListener();
  }

  private void mouseHandlerTable(MouseEvent evt)
  {
      dbg.println(9, "OptionsDialog - mouseHandlerTable evt=" + evt.toString());
      dbg.println(9, "  findComponentAt="+findComponentAt(evt.getX(), evt.getY()).toString());
      if (evt.getID() == MouseEvent.MOUSE_CLICKED)
      {
          int rowAtPoint = table.rowAtPoint(evt.getPoint());
          int colAtPoint = table.columnAtPoint(evt.getPoint());
          dbg.dprintf(9, "  rowAtPoint=%d colAtPoint=%d\n", rowAtPoint, colAtPoint);
      }

  }

  private void tableChangedHandler(TableModelEvent evt)
  {
      dbg.println(9, "tableChangedHandler evt=" + evt);
      dbg.println(19, "  UPDATE=" + TableModelEvent.UPDATE);
  }

  void okHandler()
  {
    try {
        for (int i = 0; i < odrlh.getRowCount(); i++)
        {
            RowHandler row = odrlh.get(i);
            row.setValue(table.getValueAt(i, colValue));
        }

        for (int i = 0; i < odrlh.getRowCount(); i++)
        {
            RowHandler row = odrlh.get(i);
            row.update();
        }

        IotComPort.reinit();

        setVisible(false);

        IotTerminalPrefs.put("OptionsDialogX", getX());
        IotTerminalPrefs.put("OptionsDialogY", getY());
        IotTerminalPrefs.put("OptionsDialogH", getHeight());
        IotTerminalPrefs.put("OptionsDialogW", getWidth());
    }catch (Exception e)
    {
      dbg.println(2, "OptionDialog.okHandler.Exception="+e.toString());
      JOptionPane.showMessageDialog(parent, e.getMessage(),
                                    "Options", JOptionPane.WARNING_MESSAGE);
    }
  }

  void cancelHandler()
  {
    setVisible(false);
    //dispose();
  }

  void addEscapeListener() {
      ActionListener escListener = new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              cancelHandler();
          }
      };
      getRootPane().registerKeyboardAction(escListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
  }

  JTable table;
  private static final long serialVersionUID = -4777973355120139808L;
}
