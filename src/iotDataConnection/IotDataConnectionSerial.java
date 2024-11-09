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
        }catch (NoSuchPortException e)
        {
            throw new Exception("IotDataConnectionSerial - NoSuchPortException portName=" + portName);
        }catch (PortInUseException e)
        {
            throw new Exception("IotDataConnectionSerial - PortInUseException portName=" + portName);
        }catch (UnsupportedCommOperationException e)
        {
            throw new Exception("IotDataConnectionSerial - UnsupportedCommOperationException portName=" + portName);
        }catch (IOException e)
        {
            throw new Exception("IotDataConnectionSerial - IOException portName=" + portName);
        }
        t = new Timer(IotComPort.getPollingTime(), this); // polling time in ms
        t.setRepeats(true);
        t.start();
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
            //e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            dbg.println(1, "IotDataConnectionSerial.close exception e=" + e.toString());
            e.printStackTrace();
        }
        inStream = null;
        outStream = null;
        serialPort.close();
        serialPort = null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DataBufferItem buffer = dbh.get(64);
        try {
            int num = inStream.read(buffer.get());
            if (num != 0)
                dbg.println(19, "IotDataConnectionSerial.actionPerformed num=" + num);
            if (num <= 0) {
                buffer.release();
                return;
            }
            buffer.set(num);
            parent.rxCallback(buffer.get(), num);
            buffer.release();
        } catch (IOException e1) {
            dbg.println(1, "IotDataConnectionSerial.actionPerformed exception e=" + e1.toString());
        }
    }

    Timer t;
    SerialPort serialPort;
    OutputStream outStream;
    InputStream inStream;
    IotDataConnectionRxIf parent;
    DataBufferHandler dbh = new DataBufferHandler();
}
