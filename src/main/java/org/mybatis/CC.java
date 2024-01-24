package org.mybatis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class CC implements Runnable {
    CCT connectionController;
    public CC(CCT connectionController) {
       this.connectionController= connectionController;
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (PID peerID : connectionController.getConnectedPeers()) {
                    if (connectionController.getChannels().isEmpty() || (isNotConnected(peerID) && !peerID.equals(connectionController.getChannels().get(0).getPeerID()))) {
                        Socket socket = new Socket(peerID.getIpAddress(), peerID.getPort());
                        CH channel = new CH(socket, peerID,connectionController);
                        connectionController.getChannels().add(channel);
                        // todo Start a HealthChecker thread for the new channel

                        System.out.println("Ahora podrás comunicarte");
                        new Thread(channel).start();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean isNotConnected(PID peerID) {
        return !connectionController.getChannels().stream().anyMatch(channel -> channel.getPeerID().equals(peerID));
    }
    public void connectToServer(String ipAddress, int port) throws IOException {
        Socket socket = new Socket(ipAddress, port);
        PID peerID = new PID("Client", socket.getInetAddress().getHostAddress(), socket.getPort());
        this.connectionController.getConnectedPeers().add(peerID);
    }


}
