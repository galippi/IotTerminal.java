package iotDriver;

import java.util.Vector;

import org.json.JSONObject;

import lippiWare.utils.dbg;

import org.json.JSONArray;

public class IotActivatedDriverList {
    public static void add(IotDriverBase driver) {
        driverList.add(driver);
    }

    public static void start(IotDriverDataCollector dc) throws Exception {
        try {
            for (IotDriverBase driver : driverList) {
                driver.start(dc);
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

    static final int version = 1;
    public static JSONObject getJson() {
        JSONObject json = new JSONObject();
        json.put("version", "" + version);
        JSONArray devices = new JSONArray();
        json.put("devices", devices);
        for (int i = 0; i < driverList.size(); i++) {
            JSONObject jsonDevice = new JSONObject();
            devices.put(jsonDevice);
            jsonDevice.put("index", i);
            IotDriverBase device = get(i);
            jsonDevice.put("name", device.getName());
            jsonDevice.put("config", device.getConfig());
        }
        return json;
    }

    public static void setJson(JSONObject jsonObject) throws Exception {
        driverList.clear();
        String str = jsonObject.getString("version");
        if (!str.contentEquals("" + version)) {
            String msg = "Version mismatch! " + str + " <-> " + version;
            dbg.println(9, "IotActivatedDriverList.setJson - version check error: " + msg);
            throw new Exception(msg);
        }
        JSONArray devices = jsonObject.getJSONArray("devices");
        for (int i = 0; i < devices.length(); i++) {
            JSONObject jsonDevice = devices.getJSONObject(i);
            int idx = jsonDevice.getInt("index");
            if (idx != i) {
                String msg = "Device order mismatch! " + str + " <-> " + i;
                dbg.println(9, "IotActivatedDriverList.setJson - sanity error: " + msg);
                throw new Exception(msg);
            }
            str = jsonDevice.getString("name");
            IotDriverBase driver = IotAvailableDriverList.get(str);
            String config = jsonDevice.getString("config");
            driver.setConfig(config);
            driverList.add(driver);
        }
    }

    public static void removeDevice(int idx) {
        driverList.remove(idx);
    }

    static Vector<IotDriverBase> driverList = new Vector<>();
}
