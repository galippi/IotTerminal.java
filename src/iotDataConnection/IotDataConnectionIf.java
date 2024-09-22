package iotDataConnection;

interface IotDataConnectionIf {
    public void sendIotCommand(String cmd);
    public void close();
}
