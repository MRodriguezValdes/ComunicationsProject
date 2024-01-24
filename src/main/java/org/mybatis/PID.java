package org.mybatis;

public class PID implements Runnable{
    private String peerName;
    private String ipAddress;
    private int port;

    public PID(String peerName, String ipAddress, int port) {
        this.setPeerName(peerName);
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    public String getPeerName() {
        return peerName;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void run() {

    }
}
