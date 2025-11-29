package iotDriver;

import java.util.concurrent.ConcurrentLinkedQueue;

public class IotDriverDataCollector {
    public void putData(byte[] msg) {
        dataStore.add(new IotDriverDataBase(msg));
    }

    public void putData(IotDriverDataBase msg) {
        dataStore.add(msg);
    }

    public IotDriverDataBase getData() {
        return dataStore.poll();
    }

    public void putDebug(String msg) {
        dbgStore.add(new IotDriverDebugBase(msg));
    }

    public IotDriverDebugBase getDebug() {
        return dbgStore.poll();
    }

    ConcurrentLinkedQueue<IotDriverDataBase> dataStore = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<IotDriverDebugBase> dbgStore = new ConcurrentLinkedQueue<>();
}
