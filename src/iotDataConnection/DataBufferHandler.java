package iotDataConnection;

import java.util.concurrent.ConcurrentLinkedQueue;

class DataBufferItem {
    DataBufferItem(DataBufferHandler parent, int size) {
        this.parent = parent;
        buffer = new byte[size < sizeMin ? sizeMin : size];
        usedSize = 0;
    }

    void release() {
        usedSize = 0;
        parent.release(this);
    }

    int getSize() {
        return buffer.length;
    }
    
    int getUsedSize() {
        return usedSize;
    }
    
    byte[] get() {
        return buffer;
    }
    
    void set(int usedSize) {
        if (usedSize > buffer.length)
            throw new Error("DataBufferItem.set - overflow!");
        this.usedSize = usedSize;
    }

    DataBufferHandler parent;
    int usedSize;
    byte[] buffer;

    static int sizeMin = 32;
}

class DataBufferHandler {
    DataBufferItem get(int size) {
        DataBufferItem item = items.poll();
        if (item == null) {
            item = new DataBufferItem(this, size);
        }
        return item;
    }
    
    void release(DataBufferItem item) {
        items.add(item);
    }

    ConcurrentLinkedQueue<DataBufferItem> items = new ConcurrentLinkedQueue<>();
}
