package Network;

import Network.data.ReceiveDataViaNetwork;
import Network.data.SendDataViaNetwork;
import jdbc.*;
import pojos.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


public class CommandProcessor {

    //Estas clases acceden a la base de datos
    private final JDBCClient jdbcClient;
    private final JDBCMedicalHistory jdbcMedicalHistory;
    private final JDBCSignal jdbcSignal;

    public CommandProcessor() {
        this.jdbcClient = new JDBCClient();
        this.jdbcMedicalHistory = new JDBCMedicalHistory();
        this.jdbcSignal = new JDBCSignal();
    }

    //Este metodo recibe un mensaje y según como empiece llama a un metodo o a otro para manejar la peticion
    public String handleClientRequest(String message, ReceiveDataViaNetwork receive, SendDataViaNetwork send) {

        //Mensaje vacio
        if (message == null || message.isEmpty())
            return "ERROR|Empty command";

        // Dividimos el mensaje: CMD|param1|param2|...
        String[] parts = message.split("\\|");
        CommandType cmd = CommandType.fromString(parts[0]);   //El comando

        try {

            switch (cmd) {

                case SEND_SYMPTOMS:
                    return handleSendSymptoms(parts);

                case ADD_EXTRA_INFO:
                    return handleAddExtraInfo(parts);

                case GET_HISTORY:
                    return handleGetHistory(parts);

                case SEND_ECG:
                case SEND_EMG:
                    return handleSignals(parts, cmd, receive, send);
                case DISCONNECT:
                    return "OK|Client disconnected";

                default:
                    return "ERROR| Unknown command" + cmd;
            }

        } catch (Exception ex) {
            // Si algo falla en cualquier comando → devolvemos error al cliente
            return "ERROR" + ex.getMessage();
        }
    }

    private String handleSendSymptoms(String[] parts) {

        int clientId = Integer.parseInt(parts[1]);
        String symptomsCSV = parts[2];

        // Pasamos de string de sintomas con ',' a una lista de sintomas
        List<String> symptoms = new ArrayList<>(Arrays.asList(symptomsCSV.split(",")));

        // Creamos un objeto MedicalHistory solo para síntomas
        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setClientId(clientId);
        medicalHistory.setDate(LocalDate.now());

        int recordId = jdbcMedicalHistory.addMedicalHistory(medicalHistory);
        jdbcMedicalHistory.addSymptoms(recordId, symptoms);

        return "OK|Symptoms are saved";
    }

    private String handleAddExtraInfo(String[] parts) {

        int clientId = Integer.parseInt(parts[1]);
        double height = Double.parseDouble(parts[2]);
        double weight = Double.parseDouble(parts[3]);

        jdbcClient.updateHeightWeight(clientId, height, weight);
        return "OK|Extra info saved";
    }


    private String handleGetHistory(String[] parts) {
        int clientId = Integer.parseInt(parts[1]);
        List<MedicalHistory> list = jdbcMedicalHistory.getMedicalHistoryByClientId(clientId);

        if (list.isEmpty()) {
            return "ERROR| No history found";
        }

        String response = "History:";

        for (MedicalHistory mh : list) {

            response += "ID: " + mh.getRecordId() + "\n";

            response += "DATE: " + mh.getDate() + "\n";

            if (mh.getSymptomsList() != null)
                response += "SYMPTOMS: " + mh.getSymptomsList() + "\n";

            if (mh.getObservations() != null)
                response += "OBS: " + mh.getObservations() + "\n";

        }

        return response;

    }

    private String handleSignals(String[] parts,
                                 CommandType cmd,
                                 ReceiveDataViaNetwork receive,
                                 SendDataViaNetwork send) throws Exception {

        int clientId = Integer.parseInt(parts[1]);
        int numSamples = Integer.parseInt(parts[2]);

        TypeSignal type = (cmd == CommandType.SEND_ECG)
                ? TypeSignal.ECG
                : TypeSignal.EMG;

        // tell client to send bytes
        send.sendString("OK|Send bytes");

        // receive raw bytes
        byte[] raw = receive.receiveBytes();

        Signal signal = new Signal(type, clientId);
        signal.fromByteArray(raw);

        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setClientId(clientId);
        medicalHistory.setDate(LocalDate.now());

        int recordId = jdbcMedicalHistory.addMedicalHistory(medicalHistory);
        signal.setRecordId(recordId);

        try {
            String fileName = signal.saveAsFile();
            signal.setSignalFile(fileName);

            jdbcSignal.addSignal(signal);

            return "OK|Signal saved";
        } catch (IOException e) {
            System.err.println("Error saving signal file: " + e.getMessage());
            send.sendString("Error saving signal");
        }

        return "ERROR|Signal could not be saved";
    }
}




