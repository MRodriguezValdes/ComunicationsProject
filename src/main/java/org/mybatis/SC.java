package org.mybatis;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SC implements Runnable {
    private ServerSocket serverSocket;
    private CCT connectionController;

    public SC(int port, CCT connectionController) throws IOException {
        serverSocket = new ServerSocket(port);
        this.connectionController = connectionController;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("connected " + clientSocket.getInetAddress().getHostAddress());
                PID peerID = new PID("Peer" + connectionController.getChannels().size(), clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                CH channel = new CH(clientSocket, peerID,connectionController);
                connectionController.getChannels().add(channel);
                // todo Start a HealthChecker thread for the new channel

                // Handle messages from this channel in a new thread
                new Thread(channel).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
