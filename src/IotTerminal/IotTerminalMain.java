package IotTerminal;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
//import java.util.Timer;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONObject;
import org.json.JSONTokener;

import config.DbgConfig;
import iotDriver.IotActivatedDriverList;
import iotDriver.IotAvailableDriverList;
import iotDriver.IotDriverDataBase;
import iotDriver.IotDriverDataCollector;
import iotDriver.IotDriverDebugBase;
import iotDriver.IotDriverSlcanSerial;
import iotDriver.IotDriverSlcanUdp;
import lippiWare.utils.dbg;
import version.VersionInfo;

public class IotTerminalMain extends javax.swing.JFrame {
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
                IotAvailableDriverList.add(new IotDriverSlcanSerial());
                IotAvailableDriverList.add(new IotDriverSlcanUdp());
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
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        JMenuBar jMenuBarMainMenu = new javax.swing.JMenuBar();

        JMenu jMenuFile = new javax.swing.JMenu("File");
        jMenuBarMainMenu.add(jMenuFile);

        JMenuItem m_FileOpen = new javax.swing.JMenuItem("Open");
        m_FileOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        m_FileOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              m_FileOpenActionPerformed();
            }
          });
        jMenuFile.add(m_FileOpen);

        m_FileSave = new javax.swing.JMenuItem("Save");
        m_FileSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        m_FileSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              m_FileSaveActionPerformed();
            }
          });
        m_FileSave.setEnabled(false);
        jMenuFile.add(m_FileSave);

        JMenuItem m_FileSaveAs = new javax.swing.JMenuItem("Save as");
        m_FileSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK + java.awt.event.InputEvent.ALT_DOWN_MASK));
        m_FileSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              m_FileSaveAsActionPerformed();
            }
          });
        jMenuFile.add(m_FileSaveAs);

        jMenuFile.add(new javax.swing.JPopupMenu.Separator());

        jMenuRecentFiles = new javax.swing.JMenu();
        jMenuRecentFiles.setText("Recent Files");
        jMenuRecentFiles.setToolTipText("");
        jMenuRecentFiles.setActionCommand("recentFiles");
        fillRecentFiles(jMenuRecentFiles);
        jMenuFile.add(jMenuRecentFiles);

        jMenuFile.add(new javax.swing.JPopupMenu.Separator());

        JMenuItem m_FileExit = new javax.swing.JMenuItem("Exit");
        m_FileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_DOWN_MASK));
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

    protected void m_FileOpenActionPerformed() {
        dbg.println(9, "IotTerminalMain.m_FileOpenActionPerformed");

        final JFileChooser fc = new JFileChooser();

        fc.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                    "IOT measurement setup file", "json"));

        String lastFileName = IotTerminalPrefs.getRecentFile(0, null);
        if (lastFileName != null) {
            File file = new File(lastFileName);
            fc.setCurrentDirectory(file.getParentFile());
        }
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
          java.io.File file = fc.getSelectedFile();
          dbg.println(9, "IotTerminalMain.m_FileOpenActionPerformed opening: " + file.getName() + ".");
          openSetupFile(file.getPath());
        } else
        {
          dbg.println(9, "IotTerminalMain.m_FileOpenActionPerformed - Open command cancelled by user.");
        }
    }

    private void openSetupFile(String fileName) {
        InputStream is;
        try {
            is = new FileInputStream(fileName);
            JSONTokener tokener = new JSONTokener(is);
            JSONObject jsonObject = new JSONObject(tokener);
            IotActivatedDriverList.setJson(jsonObject);
            this.setTitle("IotTerminal - " + fileName);
            measConfigFilename = fileName;
            updateRecentFileList(fileName);
            m_FileSave.setEnabled(true);
        } catch (Exception e) {
            String errorMsg = "IotTerminalMain.openSetupFile - unable to open or load file " + fileName + "!\ne=" + e.toString() + "\n";
            dbg.println(1, errorMsg);
            JOptionPane.showMessageDialog(IotDeviceSetupDlg.idsd, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    String measConfigFilename;

    private void m_FileSaveActionPerformed() {
        dbg.println(9, "IotTerminalMain.m_FileSaveActionPerformed");
        if (measConfigFilename != null)
        {
          dbg.println(9, "IotTerminalMain.m_FileSaveActionPerformed saving: " + measConfigFilename + ".");
          saveSetupFile(measConfigFilename);
        } else
        {
          dbg.println(9, "IotTerminalMain.m_FileSaveActionPerformed - Save command cancelled by user.");
          JOptionPane.showMessageDialog(IotDeviceSetupDlg.idsd, "No config file is selected, therefore the config is not saved!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveSetupFile(String filename) {
        //final String fileNameExtension = "json";
        try {
            FileWriter myWriter = new FileWriter(filename);
            JSONObject jsonObject = IotActivatedDriverList.getJson();
            myWriter.write(jsonObject.toString(2));
            myWriter.close();
            dbg.dprintf(9, "IotTerminalMain.saveSetupFile(%s) done!\n", filename);
            measConfigFilename = filename;
            this.setTitle("IotTerminal - " + filename);
            updateRecentFileList(filename);
            m_FileSave.setEnabled(true);
        } catch (Exception e) {
            dbg.dprintf(1, "IotTerminalMain.saveSetupFile exception (%s) e=%s!\n", filename, e.toString());
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Unable to save measurement setup " + filename +"!\nDetailed info: " + e.toString(),
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void m_FileSaveAsActionPerformed() {
        dbg.println(9, "IotTerminalMain.m_FileSaveActionPerformed");

        final JFileChooser fc = new JFileChooser();
        fc.setDialogType(JFileChooser.SAVE_DIALOG);

        fc.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter(
                    "IOT measurement setup file", "json"));

        String lastFileName = IotTerminalPrefs.getRecentFile(0, null);
        if (lastFileName != null) {
            File file = new File(lastFileName);
            fc.setCurrentDirectory(file.getParentFile());
        }

        int returnVal = fc.showDialog(this, "Save as");
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
          java.io.File file = fc.getSelectedFile();
          dbg.println(9, "IotTerminalMain.m_FileSaveActionPerformed saving: " + file.getName() + ".");
          try {
              FileNameExtensionFilter ff = (FileNameExtensionFilter)fc.getFileFilter();
              String defaultExt = ff.getExtensions()[0];
              String filename = file.getPath();
              int idxDot = filename.lastIndexOf('.');
              if (idxDot >= 0) {
                  String ext = filename.substring(idxDot + 1);
                  if (!ext.equalsIgnoreCase(defaultExt))
                      idxDot = -1;
              }
              if (idxDot < 0) {
                  filename = filename + '.' + defaultExt;
                  file = new File(filename);
              }
          }catch (Exception e) { // no file type is selected
              dbg.println(9, "IotTerminalMain.m_FileSaveAsActionPerformed FileNameExtensionFilter exception e=" + e.toString());
          }
          if (file.exists()) {
              int answer = JOptionPane.showConfirmDialog(
                      this,
                      "The file already exist! Would you like to overwrite it?",
                      "Warning",
                      JOptionPane.OK_CANCEL_OPTION);
              dbg.println(9, "IotTerminalMain.m_FileSaveActionPerformed warning answer=" + answer);
              if (answer != JOptionPane.OK_OPTION) {
                  dbg.println(9, "IotTerminalMain.m_FileSaveActionPerformed SavAs is aborted");
                  return;
              }
          }
          saveSetupFile(file.getPath());
        } else
        {
          dbg.println(9, "IotTerminalMain.m_FileSaveActionPerformed - Save command cancelled by user.");
        }
    }

    IotDriverDataCollector dc;

    protected void m_MeasStartActionPerformed(ActionEvent evt) {
        dbg.println(9, "IotTerminalMain.m_MeasStartActionPerformed");
        dc = new IotDriverDataCollector();
        if (IotActivatedDriverList.size() > 0)
            try {
                IotActivatedDriverList.start(dc);
                m_MeasStart.setEnabled(false);
                m_MeasStop.setEnabled(true);
                timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        timerEventHandler();
                    }});
                timer.setRepeats(true);
                timer.start();
            }catch(Exception e) {
                dbg.println(9, "IotTerminalMain.m_MeasStartActionPerformed e=" + e.toString());
                IotActivatedDriverList.stop();
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Unable to start measurement!\nDetailed info: " + e.toString(),
                        "Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        else
            JOptionPane.showMessageDialog(IotDeviceSetupDlg.idsd, "No device is configured!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void timerEventHandler() {
        dbg.println(9, "IotTerminalMain.timer.actionPerformed " + Thread.currentThread().toString());
        if (dc == null) {
            //timer.stop();
        }else
            processData(dc);
    }

    Timer timer;

    protected void m_MeasStopActionPerformed(ActionEvent evt) {
        dbg.println(9, "m_MeasStopActionPerformed");
        IotActivatedDriverList.stop();
        m_MeasStart.setEnabled(true);
        m_MeasStop.setEnabled(false);
        IotDriverDataCollector dcCopy = dc;
        timer.stop();
        timer = null;
        dc = null;
        processData(dcCopy);
    }

    private void processData(IotDriverDataCollector dc) {
        IotDriverDataBase data;
        while ((data = dc.getData()) != null) {
            dbg.println(9, data.toString());
            addLog(data.toString());
        }

        IotDriverDebugBase debug;
        while ((debug = dc.getDebug()) != null) {
            dbg.println(9, debug.msg);
            addLog(debug.msg);
        }
    }

    protected void m_DeviceSetupActionPerformed(ActionEvent evt) {
        dbg.println(9, "m_DeviceSetupActionPerformed");
        IotDeviceSetupDlg idsd = new IotDeviceSetupDlg(this);
        idsd.setVisible(true);
    }

    public void addLog(String msg) {
        mainPanel.addLog(msg);
    }

    private void fillRecentFiles(JMenu jMenuRecentFiles) {
        jMenuRecentFiles.removeAll();
        int nextRecentFile = 0;
        for (int i = 0; i < 10; i++)
        {
          String val = IotTerminalPrefs.getRecentFile(i, "");
          if (!val.isEmpty())
          {
            javax.swing.JMenuItem jMenuItem = new javax.swing.JMenuItem();
            jMenuItem.setText(nextRecentFile + ": " + val);
            nextRecentFile++;
            jMenuItem.addActionListener(new java.awt.event.ActionListener() {
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_RecentFileActionPerformed(evt);
              }
            });
            jMenuRecentFiles.add(jMenuItem);
          }
        }
    }

    protected void m_RecentFileActionPerformed(ActionEvent evt) {
        dbg.println(9, "m_RecentFileActionPerformed " + evt.toString());
        String val = evt.getActionCommand();
        dbg.dprintf(9, "  val=%s\n", val);
        if (val.charAt(1) == ':')
        {
          int idx = val.charAt(0) - '0';
          String file = val.substring(3);
          dbg.dprintf(9, "  idx=%d file=%s\n", idx, file);
          openSetupFile(file);
        }
    }
    
    void updateRecentFileList(String fileName) {
        int i;
        for (i = 0; i < 10; i++)
        { /* check the existence of the file on the recent list */
            if (fileName.equals(IotTerminalPrefs.getRecentFile(i, "")))
            { // the file is already on the recent list -> move it to the first position
                break;
            }
        }
        if (i != 0)
        {
            for (; i > 0; i--)
            { /* move recent file lower */
                IotTerminalPrefs.putRecentFile(i, IotTerminalPrefs.getRecentFile(i - 1, ""));
            }
            IotTerminalPrefs.putRecentFile(0, fileName);
            fillRecentFiles(jMenuRecentFiles);
        }
    }

    public void windowClose(java.awt.event.WindowEvent e)
    {
      dbg.println(9, "windowClose");
      IotActivatedDriverList.close();
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
    private JMenuItem m_FileSave;
    private JMenu jMenuRecentFiles;
    static JMenuItem m_MeasStart;
    static JMenuItem m_MeasStop;

    private static final long serialVersionUID = -240137363597989690L;
}
