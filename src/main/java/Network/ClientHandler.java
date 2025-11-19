package Network;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (DataInputStream dataIn = new DataInputStream(socket.getInputStream());
             DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream())) {

            boolean running = true;

            while (running) {
                String message = dataIn.readUTF();

                if (message.startsWith(String.valueOf(CommandType.SEND_SYMPTOMS))) {

                    String[] parts = message.split("\\|");
                    int clientId = Integer.parseInt(parts[1]);
                    String[] symptoms = parts[2].split(",");

                    System.out.println("Symptoms received " + clientId);
                    dataOut.flush();
                } else if (message.startsWith(String.valueOf(CommandType.SEND_ECG))) { // falta añadir EMG

                    String[] parts = message.split("\\|");
                    int clientId = Integer.parseInt(parts[1]);
                    String type = parts[2];
                    int numSamples = Integer.parseInt(parts[3]);


                    // Read Bytes form the signal
                    int length = dataIn.readInt();
                    byte[] buffer = new byte[length];
                    dataIn.readFully(buffer);

                    System.out.println("Signal " + type + " received (" + numSamples + " samples)");
                    dataOut.flush();

                } else if (message.equals("DISCONNECT")) {
                    dataOut.flush();
                    running = false;
                }
            }

        } catch (IOException e) {
            System.out.println("ClientDisconnected: " + e.getMessage());
        }
    }
}

