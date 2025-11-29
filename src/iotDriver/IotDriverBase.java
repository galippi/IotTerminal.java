package iotDriver;

import java.util.Vector;

public abstract class IotDriverBase {

    public int getSubchannelNumber() {
        return 1;
    }

    public abstract void start(IotDriverDataCollector dc) throws Exception;
    public abstract void stop();

    public void close() {
        
    }

    public void init(int channelIdx, Object initData) {
        
    }

    public void checkConfig(final Vector<IotDriverBase> driverList) {
        
    }

    public abstract IotDriverBase create();

    public abstract String getName();

    public IotDriverBaseConfigDlg getConfigDlg() {
        return null;
    }

    public String getConfig() {
        return "";
    }

    public void setConfig(String config) throws Exception {
        
    }

    public String checkDevice() {
        return "(not yet implemented)";
    }
}
