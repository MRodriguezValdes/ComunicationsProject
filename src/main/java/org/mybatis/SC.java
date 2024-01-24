package org.mybatis;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SC implements Runnable {
    private ServerSocket serverSocket;
    private CCT connectionController;

    public SC(int port,CCT connectionController) throws IOException {
        serverSocket = new ServerSocket(port);
        this.connectionController= connectionController;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("connected " + clientSocket.getInetAddress().getHostAddress());
                PID peerID = new PID("Peer" + connectionController.getChannels().size(), clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                CH channel = new CH(clientSocket, peerID);
                connectionController.getChannels().add(channel);
                // Start a HealthChecker thread for the new channel
//                HC healthChecker = new HC(channel);
//                new Thread(healthChecker).start();

                // Handle messages from this channel in a new thread
                new Thread(() -> {
                    try {
                        handleMessages(channel);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleMessages(CH channel) throws IOException {
        try {
            while (true) {
                String message = channel.receiveMessage();
                if (message!=null){
                    System.out.println("Recibo como servidor");
                    System.out.println(channel.getPeerID().getPeerName() + ": " + message);
                }
            }
        } catch (IOException e) {
            // Handle disconnection
            System.out.println("adios chanel");
            channel.close();
            connectionController.getChannels().remove(channel);
        }
    }
}
