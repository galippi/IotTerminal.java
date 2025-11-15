package iotDriver;

import java.util.Vector;

public class IotAvailableDriverList {
    public static void add(IotDriverBase driver) {
        if (driverList == null)
            driverList = new Vector<>();
        driverList.add(driver);
    }

    public static int size() {
        return driverList.size();
    }

    public static IotDriverBase get(int idx) {
        return driverList.get(idx);
    }

    static Vector<IotDriverBase> driverList;
}
