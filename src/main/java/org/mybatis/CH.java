    package org.mybatis;

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.io.PrintWriter;
    import java.net.Socket;

    public class CH {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private PID peerID;

        public CH(Socket socket, PID peerID) throws IOException {
            this.socket = socket;
            this.peerID = peerID;

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
    }
