package iotDriver;

import java.util.Vector;

public interface IotDriverBase {
    int getSubchannelNumber();
    void start() throws Exception;
    void stop();
    void close();
    void init(int channelIdx, Object initData);
    void checkConfig(final Vector<IotDriverBase> driverList);
    IotDriverBase create();
    String getName();
    IotDriverBaseConfigDlg getConfigDlg();
    String getConfig();
    void setConfig(String config) throws Exception;
    String checkDevice();
}
