package Network;

import Network.data.ReceiveDataViaNetwork;
import Network.data.SendDataViaNetwork;
import pojos.MedicalHistory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket socket;
    private final ReceiveDataViaNetwork receive;
    private final SendDataViaNetwork send;


    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.receive = new ReceiveDataViaNetwork(socket);
        this.send = new SendDataViaNetwork(socket);
    }

    @Override
    public void run() {
        try{
            boolean running = true;
            while (running) {
                String message = receive.receiveString();
                if (message.startsWith(String.valueOf(CommandType.SEND_SYMPTOMS))) {

                    String[] parts = message.split("\\|");
                    int clientId = Integer.parseInt(parts[1]);
                    List<String> symptoms = Arrays.asList(parts[2].split(","));

                    MedicalHistory medicalHistory = new MedicalHistory();
                    medicalHistory.setClientId(clientId);
                    medicalHistory.setDate(LocalDate.now());
                    medicalHistory.setSymptomsList(symptoms);

                    //FALTA IMPLEMENTRA JDBC 

                    System.out.println("Symptoms received from client: " + clientId);
                    dataOut.writeUTF("Received");
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
                    dataOut.writeUTF("Received");
                    dataOut.flush();

                } else if (message.equals("DISCONNECT")) {
                    dataOut.writeUTF("Closing");
                    dataOut.flush();
                    running = false;
                }
            }

        } catch (IOException e) {
            System.out.println("ClientDisconnected: " + e.getMessage());
        }
    }
}

