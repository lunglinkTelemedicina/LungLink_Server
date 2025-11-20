package Network;

import Network.data.ReceiveDataViaNetwork;
import Network.data.SendDataViaNetwork;
import jdbc.*;
import jdbc.JDBCMedicalHistory;
import jdbc.JDBCSignal;
import pojos.*;
import pojos.TypeSignal;

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

    private final JDBCMedicalHistory jdbcMedicalHistory = new JDBCMedicalHistory();
    private final JDBCSignal jdbcSignal = new JDBCSignal();
    private final JDBCClient jdbcClient = new JDBCClient();


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
                    jdbcMedicalHistory.addMedicalHistory(medicalHistory);
                    jdbcMedicalHistory.addSymptoms(medicalHistory.getRecordId(), symptoms);
                    send.sendString("Symptoms saved correctly");


                } else if (message.startsWith("SEND_ECG") || message.startsWith("SEND_EMG")) {

                    String[] parts = message.split("\\|");
                    int clientId = Integer.parseInt(parts[1]);
                    int numSamples = Integer.parseInt(parts[2]);
                    TypeSignal type = message.startsWith("SEND_ECG") ? TypeSignal.ECG : TypeSignal.EMG;

                    send.sendString("Client can send the data");

                    byte[] raw = receive.receiveBytes();

                    // Convertir bytes a enteros (raw BITalino)
                    Signal signal = new Signal();
                    signal.setClientId(clientId);
                    signal.setType(type);
                    signal.fromByteArray(raw);

                    // Guardar en un historial nuevo
                    MedicalHistory mh = new MedicalHistory();
                    mh.setClientId(clientId);
                    mh.setDate(LocalDate.now());
                    jdbcMedicalHistory.addMedicalHistory(mh);

                    int recordId = jdbcMedicalHistory.addMedicalHistory(mh);
                    signal.setRecordId(recordId);

                    jdbcMedicalHistory.addSignalToMedicalHistory(mh.getRecordId(), signal);

                    send.sendString("Signal saved");

                }else if (message.startsWith(String.valueOf(CommandType.ADD_EXTRA_INFO))) {

                    String[] parts = message.split("\\|");
                    int clientId = Integer.parseInt(parts[1]);
                    double height = Double.parseDouble(parts[2]);
                    double weight = Double.parseDouble(parts[3]);

                    jdbcClient.updateHeightWeight(clientId, height, weight);
                    send.sendString("Extra Info saved");

                }else if (message.startsWith(String.valueOf(CommandType.GET_HISTORY))){
                    String[] parts = message.split("\\|");
                    int clientId = Integer.parseInt(parts[1]);
                    List <MedicalHistory> medicalHistoryList= jdbcMedicalHistory.getMedicalHistoryByClientId(clientId);
                    StringBuilder sb = new StringBuilder();
                    for (MedicalHistory mh : medicalHistoryList) {
                        sb.append("RecordID=").append(mh.getRecordId())
                                .append(";Date=").append(mh.getDate())
                                .append(";Obs=").append(mh.getObservations())
                                .append("\n");
                    }
                    send.sendString(sb.toString());

                } else if (message.equals("DISCONNECT")) {
                    send.sendString("Disconnected");
                    running = false;
                }
            }

        } catch (IOException e) {
            System.out.println("ClientDisconnected: " + e.getMessage());
        }
    }
}

