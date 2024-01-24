package org.mybatis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CCT {
    private List<CH> channels;
    private List<PID> connectedPeers;

    public CCT() {
        channels = new ArrayList<>();
        connectedPeers = new ArrayList<>();
    }

    public static void main(String[] args) {
        CCT controller = new CCT();
        if (args.length > 0) {
            int port = Integer.parseInt(args[0]);
            controller.startServer(port);
            if (controller.channels.isEmpty()) {
                controller.startClient();
            }
        } else {
            System.out.println("Por favor, especifique un puerto de la forma: java CTT.class <port>");
        }
    }

    public List<CH> getChannels() {
        return channels;
    }

    public List<PID> getConnectedPeers() {
        return connectedPeers;
    }

    public void startServer(int initialPort) {
        int port = initialPort;
        boolean serverStarted = false;

        while (!serverStarted && port <= initialPort + 10) {
            try {
                SC serverConnector = new SC(port, this);
                new Thread(serverConnector).start();
                serverStarted = true;
            } catch (IOException e) {
                System.out.println("Puerto " + port + " en uso. Probando siguiente puerto.");
                port++;
            }
        }

        if (!serverStarted) {
            System.out.println("No se pudo iniciar el servidor en ningún puerto.");
        }
    }

    public void startClient() {
        CC clientConnector = new CC(this);
        System.out.print("Por favor, indique el puerto al que desea conectarse: ");
        try {
            clientConnector.connectToServer("localhost", new Scanner(System.in).nextInt());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(clientConnector).start();
    }
}
