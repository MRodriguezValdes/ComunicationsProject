package org.mybatis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CH implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private PID peerID;
    private CCT connectionController;

    public CH(Socket socket, PID peerID, CCT connectionController) throws IOException {
        this.socket = socket;
        this.peerID = peerID;
        this.connectionController = connectionController; // Agregar esta línea

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true); // Configurar autoFlush
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public PID getPeerID() {
        return peerID;
    }

    private void handleInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                if (scanner.hasNextLine()) {
                    String inputMessage = scanner.nextLine();
                    this.sendMessage(inputMessage);
                }
            }
        }
    }

    @Override
    public void run() {
        new Thread(this::handleInput).start();
        try {
            while (true) {
                String message = receiveMessage();
                if (message != null) {
                    System.out.println(peerID.getPeerName() + ": " + message);
                }
            }
        } catch (IOException e) {
            // Manejar desconexión
            System.out.println("Se desconectó: " + peerID.getPeerName());
            try {
                close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            connectionController.getChannels().remove(this);
        }

    }
}
