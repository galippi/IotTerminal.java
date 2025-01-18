package IotTerminal;

import java.io.FileWriter;
import java.io.IOException;

import lippiWare.blfHandler.CanMessage;
import lippiWare.blfHandler.VectorAscFile;
import lippiWare.utils.LocalDateTimeMy;
import lippiWare.utils.dbg;

public class IotDataLogger {
    static void open(String _filename) {
        filename = _filename;
        try {
            myWriter = new FileWriter(filename);
            myWriter.write(VectorAscFile.header(LocalDateTimeMy.now()));
            t0 = System.nanoTime();
        } catch (IOException e) {
            dbg.println(1, "Unable to create file \'" + filename + "\'! e=" + e.toString());
            filename = null;
        }
    }

    static void close() {
        if (myWriter != null)
            try {
                myWriter.close();
            } catch (IOException e) {
                dbg.println(1, "Unable to close file \'" + filename + "\'! e=" + e.toString());
            }
        myWriter = null;
        filename = null;
    }

    static void setU0(double val) {
        if (Double.isNaN(val))
            u0 = 0xFFFF;
        else
            u0 = (int)(val * 1000);
        if (myWriter == null)
            return;
        data2[0] = (byte)(u0 & 0xFF);
        data2[1] = (byte)((u0 >> 8) & 0xFF);
        CanMessage msg = new CanMessage(0, timeStampNow(), 0x10, CanMessage.Tx, 2, data2);
        try {
            myWriter.write(VectorAscFile.toString(msg) + "\n");
        } catch (IOException e) {
            dbg.println(1, "Unable to write (setU0) to file \'" + filename + "\'! e=" + e.toString());
        }
    }

    static void setU1(double val) {
        if (Double.isNaN(val))
            u1 = 0xFFFF;
        else
            u1 = (int)(val * 1000);
        if (myWriter == null)
            return;
        data2[0] = (byte)(u1 & 0xFF);
        data2[1] = (byte)((u1 >> 8) & 0xFF);
        CanMessage msg = new CanMessage(0, timeStampNow(), 0x11, CanMessage.Tx, 2, data2);
        try {
            myWriter.write(VectorAscFile.toString(msg) + "\n");
        } catch (IOException e) {
            dbg.println(1, "Unable to write (setU1) to file \'" + filename + "\'! e=" + e.toString());
        }
    }

    static void setI(double val, double mAh) {
        if (Double.isNaN(val))
            i = 0x7FFF;
        else
            i = (int)(val * 1);
        if (myWriter == null)
            return;
        data4[0] = (byte)(i & 0xFF);
        data4[1] = (byte)((i >> 8) & 0xFF);
        int mAhi = (int)(mAh * 10); // resolution: 100 uAh
        data4[2] = (byte)(mAhi & 0xFF);
        data4[3] = (byte)((mAhi >> 8) & 0xFF);
        CanMessage msg = new CanMessage(0, timeStampNow(), 0x12, CanMessage.Tx, data4);
        try {
            myWriter.write(VectorAscFile.toString(msg) + "\n");
        } catch (IOException e) {
            dbg.println(1, "Unable to write (setI) to file \'" + filename + "\'! e=" + e.toString());
        }
    }

    public static void setDHT(double T, double hum, int dataCtr, int errCtr) {
        if (myWriter == null)
            return;
        int Ti;
        if (Double.isNaN(T))
            Ti = 0x7FFF;
        else
            Ti = (int)(T * 10);
        data8[0] = (byte)( Ti       & 0xFF);
        data8[1] = (byte)((Ti >> 8) & 0xFF);
        int humi;
        if (Double.isNaN(hum))
            humi = 0x7FFF;
        else
            humi = (int)(hum * 10);
        data8[2] = (byte)( humi       & 0xFF);
        data8[3] = (byte)((humi >> 8) & 0xFF);
        data8[4] = (byte)( dataCtr       & 0xFF);
        data8[5] = (byte)((dataCtr >> 8) & 0xFF);
        data8[6] = (byte)( errCtr       & 0xFF);
        data8[7] = (byte)((errCtr >> 8) & 0xFF);
        CanMessage msg = new CanMessage(0, timeStampNow(), 0x13, CanMessage.Tx, 8, data8);
        try {
            myWriter.write(VectorAscFile.toString(msg) + "\n");
        } catch (IOException e) {
            dbg.println(1, "Unable to write (setI) to file \'" + filename + "\'! e=" + e.toString());
        }
    }

    public static void setData(int msgId, byte[] data) {
        if (myWriter == null)
            return;
        CanMessage msg = new CanMessage(0, timeStampNow(), msgId, CanMessage.Tx, data.length, data);
        try {
            myWriter.write(VectorAscFile.toString(msg) + "\n");
        } catch (IOException e) {
            dbg.println(1, "Unable to write (setData) to file \'" + filename + "\'! e=" + e.toString());
        }
    }

    private static long timeStampNow() {
        long dt = System.nanoTime() - t0;
        return dt;
    }

    static String filename;
    static FileWriter myWriter;
    private static long t0;
    static int u0 = 0xFFFF;
    static int u1 = 0xFFFF;
    static int i = 0xFFFF;
    static byte[] data2 = new byte[2];
    static byte[] data4 = new byte[4];
    static byte[] data8 = new byte[8];
}
