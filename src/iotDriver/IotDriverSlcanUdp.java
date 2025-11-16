package iotDriver;

import java.util.Vector;

public class IotDriverSlcanUdp extends IotDriverBase {

    @Override
    public int getSubchannelNumber() {
        return 1;
    }

    @Override
    public void start() throws Exception {
        throw new Exception("IotDriverSlcanSerial - Not yet implemented!");
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void init(int channelIdx, Object initData) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void checkConfig(Vector<IotDriverBase> driverList) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public IotDriverBase create() {
        return new IotDriverSlcanUdp();
    }

    @Override
    public String getName() {
        return "Slcan UDP";
    }

    @Override
    public IotDriverBaseConfigDlg getConfigDlg() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setConfig(String config) throws Exception {
        // TODO Auto-generated method stub
        throw new Exception("IotDriverSlcanUdp.setConfig - not yet implemented config=" + config);
    }

    @Override
    public String checkDevice() {
        // TODO Auto-generated method stub
        return null;
    }
}
