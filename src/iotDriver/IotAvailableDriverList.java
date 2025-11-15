package iotDriver;

import java.util.Vector;

import lippiWare.utils.dbg;

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

    public static IotDriverBase get(String str) {
        for (int i = 0; i < driverList.size(); i++) {
            IotDriverBase driver = driverList.get(i);
            if (driver.getName().contentEquals(str))
                return driver.create();
        }
        dbg.println(9, "Unable to find driver " + str);
        return null;
    }

    static Vector<IotDriverBase> driverList;
}
