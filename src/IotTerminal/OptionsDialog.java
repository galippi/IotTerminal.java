/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IotTerminal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
public class OptionsDialog extends JDialog {

  //headers for the table
  final String[] columnNames = new String[] {
      "Property name", "Property value"
  };
  final int colPropertyValue = 1;
  final int rowDebugLevel = 0;
  final int rowComPortName = 1;
  final int rowComPortBaudRate = 2;
  final int rowComPortPollingTime = 3;

  IotTerminalMain parent;

  OptionsDialog(IotTerminalMain _parent)
  {
    super(_parent, Dialog.ModalityType.APPLICATION_MODAL);
    parent = _parent;
    this.setTitle("Options");

    //create table with data
    table = new JTable(new DefaultTableModel(4, 2) {
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
    table.setValueAt("Debug level:", rowDebugLevel, 0);
    table.setValueAt("" + dbg.get(), rowDebugLevel, colPropertyValue);
    table.setValueAt("COM port:",              rowComPortName, 0);
    table.setValueAt(IotComPort.getPortName(), rowComPortName, colPropertyValue);
    table.setValueAt("Baud rate:",                rowComPortBaudRate, 0);
    table.setValueAt(""+IotComPort.getBaudRate(), rowComPortBaudRate, colPropertyValue);
    table.setValueAt("Polling time of COM port [ms]:", rowComPortPollingTime, 0);
    table.setValueAt(""+IotComPort.getPollingTime(),   rowComPortPollingTime, colPropertyValue);

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
    boolean closable = true;
    int level = -1;
    String comPortName = "";
    int baudRate = 0;
    int pollingTime = -1;
    try {
      level = Integer.parseInt((String) table.getValueAt(rowDebugLevel, colPropertyValue));
      if (level < 0)
          throw new Exception("Invalid debug level");
      comPortName = (String) table.getValueAt(rowComPortName, colPropertyValue);
      if ((comPortName.length() < 1) || (!IotComPort.isComPortValid(comPortName)))
          throw new Exception("Invalid COM port name");
      baudRate = Integer.parseInt((String) table.getValueAt(rowComPortBaudRate, colPropertyValue));
      if ((baudRate < 100) || (baudRate > 10000000))
          throw new Exception("Invalid baud rate");
      pollingTime = Integer.parseInt((String) table.getValueAt(rowComPortPollingTime, colPropertyValue));
      if ((pollingTime < 10) || (pollingTime > 5000))
          throw new Exception("Invalid polling time");
    }catch (Exception e)
    {
      dbg.println(2, "OptionDialog.okHandler.Exception="+e.toString());
      JOptionPane.showMessageDialog(parent, e.getMessage(),
                                    "Options", JOptionPane.WARNING_MESSAGE);
      closable = false;
    }

    if (closable)
    {
        IotTerminalPrefs.put("Debug level", level);
        dbg.set(level);

        IotComPort.openPort(comPortName);
        //parent.setBackgroundColor(backgroundColor);

        IotComPort.setBaudRate(baudRate);

        IotComPort.setPollingTime(pollingTime);

        IotComPort.reinit();

        IotTerminalPrefs.put("OptionsDialogX", getX());
        IotTerminalPrefs.put("OptionsDialogY", getY());
        IotTerminalPrefs.put("OptionsDialogH", getHeight());
        IotTerminalPrefs.put("OptionsDialogW", getWidth());

        setVisible(false);
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
