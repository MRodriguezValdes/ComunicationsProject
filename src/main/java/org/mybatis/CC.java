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
        new Thread(this::handleInput).start();

        while (true) {
            try {
                for (PID peerID : connectionController.getConnectedPeers()) {
                    if (connectionController.getChannels().isEmpty() || (isNotConnected(peerID) && !peerID.equals(connectionController.getChannels().get(0).getPeerID()))) {
                        Socket socket = new Socket(peerID.getIpAddress(), peerID.getPort());
                        CH channel = new CH(socket, peerID);
                        connectionController.getChannels().add(channel);
                        // todo Start a HealthChecker thread for the new channel

                        System.out.println("Ahora podrás comunicarte");
                        new Thread(() -> {
                            try {
                                handleMessages(channel);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }).start();
                    }
                }
                Thread.sleep(5000); // Esperar 5 segundos antes de intentar la reconexión
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void handleMessages(CH channel) throws IOException {
        try {
            while (true) {
                String message = channel.receiveMessage();
                if (message != null) {
                    System.out.println(channel.getPeerID().getPeerName() + ": " + message);
                }
            }
        } catch (IOException e) {
            // Manejar desconexión
            System.out.println("Se desconectó: " + channel.getPeerID().getPeerName());
            channel.close();
            connectionController.getChannels().remove(channel);
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
    private void handleInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                if (scanner.hasNextLine()) {
                    String inputMessage = scanner.nextLine();
                    for (CH channel : connectionController.getChannels()) {
                        channel.sendMessage(inputMessage);
                    }
                }
            }
        }
    }

}
