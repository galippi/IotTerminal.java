package iotDriver;

import java.util.Vector;

public class IotDriverSlcanUdp implements IotDriverBase {

    @Override
    public int getSubchannelNumber() {
        return 1;
    }

    @Override
    public void start() throws Exception {
        // TODO Auto-generated method stub
        
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

}
