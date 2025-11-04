package IotTerminal;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import config.DbgConfig;
import iotDataConnection.IotDataConnectionIf;
import iotDataConnection.IotDataConnectionSerial;
import iotDriver.IotDriverList;
import iotDataConnection.IotDataConnectionRxIf;
import lippiWare.utils.bin;
import lippiWare.utils.dbg;
import lwLogDataProcessor.LogDataProcessor;
import lwLogDataProcessor.LogDataProcessorDefaultHandler;
import lwLogDataProcessor.LogDataProcessorHandlerBase;
import lwLogDataProcessor.LogDataProcessorHexu16Base;
import lwLogDataProcessor.LogDataProcessorHexu8ArrayBase;
import version.VersionInfo;

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

class IotDht11Handler extends LogDataProcessorHexu8ArrayBase {
    IotDht11Handler(String prefix, IotGaugeDht11 parent) {
        super(prefix);
        this.parent = parent;
    }

    @Override
    public void process(byte[] data, String rest) {
        parent.setValue(data);
    }

    IotGaugeDht11 parent;
}

class IotDbg00Handler extends LogDataProcessorHandlerBase {
    IotDbg00Handler(String prefix, IotGaugeString parent) {
        super(prefix);
        this.parent = parent;
    }

    @Override
    public void process(String data) {
        parent.setValue(data);
    }

    IotGaugeString parent;
}

public class IotTerminalMain extends javax.swing.JFrame implements IotDataConnectionRxIf {
    public static void main(String[] args) {
        //dbg.set(IotTerminalPrefs.get("Debug level", 1));
        dbg.setLevelMask(DbgConfig.dbgLevelMask);
        int level = IotTerminalPrefs.get("Debug level", 0x3F);
        dbg.set(level);

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
        for (int i = 0; true; i++) {
            IotGaugePanel p = mainPanel.getPanel(i);
            if (p == null)
                break;
            ldp.addHandler(p.getLogDataProcessorHandler());
        }
        try {
            //String path = System.getenv("PATH");
            iotDataConnection = new IotDataConnectionSerial(this);
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
        jMenuBarMainMenu.add(jMenuFile);

        JMenuItem m_FileExit = new javax.swing.JMenuItem("Exit");
        m_FileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        m_FileExit.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            m_FileExitActionPerformed(evt);
          }
        });
        jMenuFile.add(m_FileExit);

        JMenu jMenuMeas = new javax.swing.JMenu("Measurement");
        jMenuBarMainMenu.add(jMenuMeas);

        m_MeasStart = new javax.swing.JMenuItem("Start");
        jMenuMeas.add(m_MeasStart);
        m_MeasStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              m_MeasStartActionPerformed(evt);
            }
          });

        m_MeasStop = new javax.swing.JMenuItem("Stop");
        jMenuMeas.add(m_MeasStop);
        m_MeasStop.setEnabled(false);
        m_MeasStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              m_MeasStopActionPerformed(evt);
            }
          });

        JMenu jMenuDevice = new javax.swing.JMenu("Device");
        jMenuBarMainMenu.add(jMenuDevice);

        JMenuItem m_DeviceSetup = new javax.swing.JMenuItem("Setup");
        jMenuDevice.add(m_DeviceSetup);
        m_DeviceSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              m_DeviceSetupActionPerformed(evt);
            }
          });

        JMenu jMenuTools = new javax.swing.JMenu("Tools");
        jMenuBarMainMenu.add(jMenuTools);

        JMenuItem m_ToolsOptions = new javax.swing.JMenuItem("Options");
        jMenuTools.add(m_ToolsOptions);
        m_ToolsOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              m_ToolsOptionsActionPerformed(evt);
            }
          });

        JMenu jMenuHelp = new javax.swing.JMenu("Help");
        jMenuBarMainMenu.add(jMenuHelp);

        JMenuItem m_HelpAbout = new javax.swing.JMenuItem("About");
        jMenuHelp.add(m_HelpAbout);
        m_HelpAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                String msgAbout = "IotTerminal demo application\n"
                        + "Version: " + VersionInfo.version;
                javax.swing.JOptionPane.showMessageDialog(mainPanel, msgAbout);
            }
          });

        setJMenuBar(jMenuBarMainMenu);

        mainPanel = new IotTerminalMainPanel(this);
        add(mainPanel);

        this.setMinimumSize(new Dimension(400, 300));
    }

    protected void m_MeasStartActionPerformed(ActionEvent evt) {
        dbg.println(9, "m_MeasStartActionPerformed");
        if (IotDriverList.size() > 0)
            try {
                IotDriverList.start();
                m_MeasStart.setEnabled(false);
                m_MeasStop.setEnabled(true);
            }catch(Exception e) {
                IotDriverList.stop();
            }
    }

    protected void m_MeasStopActionPerformed(ActionEvent evt) {
        dbg.println(9, "m_MeasStopActionPerformed");
        IotDriverList.stop();
        m_MeasStart.setEnabled(true);
        m_MeasStop.setEnabled(false);
    }

    protected void m_DeviceSetupActionPerformed(ActionEvent evt) {
        dbg.println(9, "m_DeviceSetupActionPerformed");
        IotDeviceSetupDlg idsd = new IotDeviceSetupDlg(this);
        idsd.setVisible(true);
    }

    LogDataProcessorDefaultHandler defaultHandler = new LogDataProcessorDefaultHandler();

    @Override
    public void rxCallback(byte[] data, int num) {
        String rxMessage = new String(data, 0, num);
        //String rxMessage = new String(data, 0, num, Charset.forName("US-ASCII"));
        addLog(rxMessage);
        if (false) {
            String str = bin.toString(data, num);
            addLog(str + "\n");
            dbg.println(11, "rxCallback str=" + str);
        }
        rxMessage = rxMessage.replace('\r', '\n');
        if (!rxMessageIsInSync) {
            int idx = rxMessage.indexOf('\n');
            if (idx < 0)
                return;
            rxMessage = rxMessage.substring(idx + 1);
            rxMessageRest = "";
            rxMessageIsInSync = true;
        }
        rxMessageRest = rxMessageRest + rxMessage;
        int idx;
        while ((idx = rxMessageRest.indexOf('\n')) >= 0) {
            if (idx > 0) {
                rxMessage = rxMessageRest.substring(0, idx);
                dbg.println(11, "Rx:" + rxMessage);
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

      IotDataLogger.close();

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

    private void m_ToolsOptionsActionPerformed(java.awt.event.ActionEvent evt) {
        dbg.println(9, "m_ToolsOptionsActionPerformed");
        OptionsDialog od = new OptionsDialog(this);
        od.setVisible(true);
      }

    static IotTerminalMain frame;
    IotTerminalMainPanel mainPanel;
    static JMenuItem m_MeasStart;
    static JMenuItem m_MeasStop;

    private static final long serialVersionUID = -240137363597989690L;
}
