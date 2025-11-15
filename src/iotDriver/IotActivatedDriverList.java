package iotDriver;

import java.util.Vector;

public class IotActivatedDriverList {
    public static void add(IotDriverBase driver) {
        driverList.add(driver);
    }

    public static void start() throws Exception {
        try {
            for (IotDriverBase driver : driverList) {
                driver.start();
            }
        }catch (Exception e)
        {
            stop();
            throw e;
        }
    }

    public static void stop() {
        for (IotDriverBase driver : driverList) {
            driver.stop();
        }
    }

    public static void close() {
        for (IotDriverBase driver : driverList) {
            driver.close();
        }
    }

    static void checkConfig() {
        for (IotDriverBase driver : driverList) {
            driver.checkConfig(driverList);
        }
    }

    public static int size() {
        return driverList.size();
    }

    public static IotDriverBase get(int idx) {
        return driverList.get(idx);
    }

    static Vector<IotDriverBase> driverList = new Vector<>();
}
