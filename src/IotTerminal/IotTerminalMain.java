package IotTerminal;

import java.awt.Dimension;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import iotDataConnection.IotDataConnection;
import iotDataConnection.IotDataConnectionIf;
import iotDataConnection.IotDataConnectionSerial;
import iotDataConnection.IotDataConnectionRxIf;
import lippiWare.utils.dbg;
import lwLogDataProcessor.LogDataProcessor;
import lwLogDataProcessor.LogDataProcessorDefaultHandler;
import lwLogDataProcessor.LogDataProcessorHexu16Base;

class IotVoltageHandler extends LogDataProcessorHexu16Base {
    IotVoltageHandler(String prefix, double factor, double offset, IotGaugeVoltage parent) {
        super(prefix);
        this.parent = parent;
        this.factor = factor;
        this.offset = offset;
    }

    @Override
    public void process(int data, String rest) {
        parent.setValue((data * factor) + offset);
    }
    IotGaugeVoltage parent;
    double factor, offset;
}

public class IotTerminalMain extends javax.swing.JFrame implements IotDataConnectionRxIf {
    public static void main(String[] args) {
        System.out.println("Haha");

        //dbg.set(IotTerminalPrefs.get("Debug level", 1));
        dbg.set(99);

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(IotTerminalMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IotTerminalMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IotTerminalMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IotTerminalMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                frame = new IotTerminalMain();
                frame.setVisible(true);
            // ------------------------------------------------------------
            // Window listener to close application when Window gets closed
            // ------------------------------------------------------------
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    dbg.println(9, "windowClosing");
                    frame.windowClose(e);
                }
            });
            }
        });
    }

    public IotTerminalMain()
    {
        setTitle("IotTerminal");
        initComponents();
        setLocation(IotTerminalPrefs.get("MainWindowX", 0), IotTerminalPrefs.get("MainWindowY", 0));
        setSize(IotTerminalPrefs.get("MainWindowW", 600), IotTerminalPrefs.get("MainWindowH", 400));
        setExtendedState(IotTerminalPrefs.get("MainWindowState", NORMAL));
        ldp.addHandler(new IotVoltageHandler("U0", 3.3/4096, 0, mainPanel.getVoltageWindow()));
        ldp.addHandler(new IotVoltageHandler("I", 0.1, 0, mainPanel.getCurrentWindow()));
        //iotDataConnection = new IotDataConnection(this);
        try {
            //String path = System.getenv("PATH");
            iotDataConnection = new IotDataConnectionSerial(this, "COM22");
        } catch (Exception e) {
            dbg.println(1, "IotTerminalMain.ctor exception e=" + e.toString());
            //e.printStackTrace();
            System.exit(1);
        }
    }
    LogDataProcessor ldp = new LogDataProcessor();
    IotDataConnectionIf iotDataConnection;

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        JMenuBar jMenuBarMainMenu = new javax.swing.JMenuBar();

        JMenu jMenuFile = new javax.swing.JMenu("File");
        JMenuItem m_FileExit = new javax.swing.JMenuItem("Exit");
        m_FileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        m_FileExit.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            m_FileExitActionPerformed(evt);
          }
        });
        jMenuFile.add(m_FileExit);

        JMenu jMenuHelp = new javax.swing.JMenu("Help");
        JMenuItem m_HelpAbout = new javax.swing.JMenuItem("About");
        jMenuHelp.add(m_HelpAbout);

        jMenuBarMainMenu.add(jMenuFile);
        jMenuBarMainMenu.add(jMenuHelp);

        setJMenuBar(jMenuBarMainMenu);

        mainPanel = new IotTerminalMainPanel(this);
        add(mainPanel);

        this.setMinimumSize(new Dimension(400, 300));
    }

    LogDataProcessorDefaultHandler defaultHandler = new LogDataProcessorDefaultHandler();

    @Override
    public void rxCallback(byte[] data, int num) {
        String rxMessage = new String(data, 0, num);
        rxMessage.replace('\r', '\n');
        if (!rxMessageIsInSync) {
            int idx = rxMessage.indexOf('\n');
            if (idx < 0)
                return;
            rxMessage = rxMessage.substring(idx + 1);
            rxMessageIsInSync = true;
        }
        rxMessageRest = rxMessageRest + rxMessage;
        int idx;
        while ((idx = rxMessageRest.indexOf('\n')) >= 0) {
            if (idx > 0) {
                rxMessage = rxMessageRest.substring(0, idx);
                ldp.process(rxMessage, defaultHandler);
            }
            rxMessageRest = rxMessageRest.substring(idx + 1);
        }
    }
    boolean rxMessageIsInSync = false;
    String rxMessageRest = "";

    public void processRxMessage(String rxMessage) {
        ldp.process(rxMessage, defaultHandler);
    }

    public void addLog(String msg) {
        mainPanel.addLog(msg);
    }

    public void sendIotCommand(String cmd) {
        iotDataConnection.sendIotCommand(cmd);
    }

    public void windowClose(java.awt.event.WindowEvent e)
    {
      dbg.println(9, "windowClose");
      iotDataConnection.close();
      this.setVisible(false);
      IotTerminalPrefs.put("MainWindowX", getX());
      IotTerminalPrefs.put("MainWindowY", getY());
      IotTerminalPrefs.put("MainWindowH", getHeight());
      IotTerminalPrefs.put("MainWindowW", getWidth());
      IotTerminalPrefs.put("MainWindowState", getExtendedState());
      mainPanel.saveWindowLayout();
      System.exit(0);
    }

    private void m_FileExitActionPerformed(java.awt.event.ActionEvent evt) {
        dbg.println(9, "m_FileExitActionPerformed");
        dispose();
        this.windowClose(null);
        System.exit(0);
      }

    static IotTerminalMain frame;
    IotTerminalMainPanel mainPanel;

    private static final long serialVersionUID = -240137363597989690L;
}
