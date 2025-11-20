package Network;

import Network.data.ReceiveDataViaNetwork;
import Network.data.SendDataViaNetwork;
import jdbc.JDBCMedicalHistory;
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

                    //FALTA IMPLEMENTRA JDBC jdbMedicalHistory.addSymptoms(medicalHistory)

                    System.out.println("Symptoms received from client: " + clientId);

                } else if (message.startsWith("SEND_ECG") || message.startsWith("SEND_EMG")) {

                    String[] parts = message.split("\\|");
                    int clientId = Integer.parseInt(parts[1]);
                    int numSamples = Integer.parseInt(parts[2]);
                    String signalType = message.startsWith("SEND_ECG") ? "ECG" : "EMG";

                    send.sendString("Client can send the data");
                    byte[] signalBytes = receive.receiveBytes();
                    //jdbcSignal.saveSignal(clientId, signalType, signalBytes)
                    send.sendString("Signal saved");

                }else if (message.startsWith(String.valueOf(CommandType.ADD_EXTRA_INFO))) {

                    String[] parts = message.split("\\|");
                    int clientId = Integer.parseInt(parts[1]);
                    double height = Double.parseDouble(parts[2]);
                    double weight = Double.parseDouble(parts[3]);

                    // jdbcClient.updateHeightWeight(clientId, height, weight);
                    send.sendString("Extra Info saved");

                }else if (message.startsWith(String.valueOf(CommandType.GET_HISTORY))){
                    String[] parts = message.split("\\|");
                    int clientId = Integer.parseInt(parts[1]);
                    List <MedicalHistory> medicalHistoryList= JDBCMedicalHistory.getMedicalHistoryByClientId(clientId);
                    try{

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }


                } else if (message.equals("DISCONNECT")) {
                    send.sendString("OK|Disconnected");
                    running = false;
                }
            }

        } catch (IOException e) {
            System.out.println("ClientDisconnected: " + e.getMessage());
        }
    }
}

