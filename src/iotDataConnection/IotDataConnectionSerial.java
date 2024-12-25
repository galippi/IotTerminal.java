package iotDataConnection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.Timer;

import gnu.io.*;
import lippiWare.utils.bin;
import lippiWare.utils.dbg;

public class IotDataConnectionSerial implements IotDataConnectionIf, ActionListener {
    public IotDataConnectionSerial(IotDataConnectionRxIf parent) throws Exception {
        this.parent = parent;
        new IotComPort(this);
        t = new Timer(1000, this); // polling time in ms
        t.setRepeats(true);
        reinit();
    }

    void open()
    {
        String portName = IotComPort.getPortName();
        try
        {
            CommPortIdentifier portId =
                    CommPortIdentifier.getPortIdentifier(portName);
          serialPort = (SerialPort) portId.open("IOT", 5000);
          // Set serial port to 115200bps-8N1
          serialPort.setSerialPortParams(
                  IotComPort.getBaudRate(),
              SerialPort.DATABITS_8,
              SerialPort.STOPBITS_1,
              SerialPort.PARITY_NONE);
          dbg.println(19, "IotDataConnectionSerial Before setFlowControlMode");
          serialPort.setFlowControlMode(
                      SerialPort.FLOWCONTROL_NONE);
          dbg.println(19, "IotDataConnectionSerial After setFlowControlMode");
          outStream = serialPort.getOutputStream();
          inStream = serialPort.getInputStream();
          return;
        }catch (NoSuchPortException e)
        {
            dbg.println(3, "IotDataConnectionSerial - NoSuchPortException portName=" + portName);
        }catch (PortInUseException e)
        {
            dbg.println(3, "IotDataConnectionSerial - PortInUseException portName=" + portName);
        }catch (UnsupportedCommOperationException e)
        {
            dbg.println(3, "IotDataConnectionSerial - UnsupportedCommOperationException portName=" + portName);
        }catch (IOException e)
        {
            dbg.println(3, "IotDataConnectionSerial - IOException portName=" + portName);
        }
        close();
    }

    @Override
    public void sendIotCommand(String cmd) {
        try {
            byte[] cmdData = cmd.getBytes();
            outStream.write(cmdData);
            dbg.println(9, "IotDataConnectionSerial.sendIotCommand tx=" + cmd);
            dbg.println(11, "IotDataConnectionSerial.sendIotCommand str=" + bin.toString(cmdData));
        } catch (IOException e) {
            dbg.println(1, "IotDataConnectionSerial.sendIotCommand exception e=" + e.toString());
            close();
            //e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            if (inStream != null)
                inStream.close();
        } catch (IOException e) {
            dbg.println(1, "IotDataConnectionSerial.close.inStream exception e=" + e.toString());
        }
        inStream = null;
        try {
            if (outStream != null)
                outStream.close();
        } catch (IOException e) {
            dbg.println(1, "IotDataConnectionSerial.close.outStream exception e=" + e.toString());
        }
        outStream = null;
        try {
            if (serialPort != null)
                serialPort.close();
        } catch (Exception e) {
            dbg.println(1, "IotDataConnectionSerial.close.serialPort exception e=" + e.toString());
        }
        serialPort = null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (serialPort == null)
            open();
        if (serialPort == null)
            return;
        DataBufferItem buffer = dbh.get(64);
        try {
            int num = inStream.read(buffer.get());
            if (num != 0)
                dbg.println(19, "IotDataConnectionSerial.actionPerformed num=" + num);
            if (num < 0)
                dbg.println(3, "IotDataConnectionSerial.actionPerformed WARNING: num=" + num);
            if (num <= 0) {
                buffer.release();
                return;
            }
            buffer.set(num);
            parent.rxCallback(buffer.get(), num);
            buffer.release();
        } catch (IOException e1) {
            dbg.println(1, "IotDataConnectionSerial.actionPerformed exception e=" + e1.toString());
            close();
        }
    }

    public void reinit()
    {
        t.stop();
        close();
        t.setDelay(IotComPort.getPollingTime());
        t.start();
        // the port will be reopened in the next timer event
    }

    Timer t;
    SerialPort serialPort;
    OutputStream outStream;
    InputStream inStream;
    IotDataConnectionRxIf parent;
    DataBufferHandler dbh = new DataBufferHandler();
}
