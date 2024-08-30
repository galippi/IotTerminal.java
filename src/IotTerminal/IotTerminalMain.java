package IotTerminal;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import iotDataConnection.IotDataConnection;
import lippiWare.utils.dbg;

public class IotTerminalMain extends javax.swing.JFrame {
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
        iotDataConnection = new IotDataConnection(this);
    }
    IotDataConnection iotDataConnection;

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

        jMenuBarMainMenu.add(jMenuFile);

        setJMenuBar(jMenuBarMainMenu);

        mainPanel = new IotTerminalMainPanel(this);
        add(mainPanel);
    }

    public void addLog(String msg) {
        mainPanel.addLog(msg);
    }

    public void windowClose(java.awt.event.WindowEvent e)
    {
      dbg.println(9, "windowClose");
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
