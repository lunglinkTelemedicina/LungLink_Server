package Network;
//TODO falta conectar bien con la base de datos cada metodo

import jdbc.JDBCClient;
import jdbc.JDBCMedicalHistory;
import jdbc.JDBCSignal;
import pojos.MedicalHistory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public String handleClientRequest(String message) {

        //Mensaje vacio
        if (message == null || message.isEmpty())
            return "ERROR|Empty command";

        // Dividimos el mensaje: CMD|param1|param2|...
        String[] parts = message.split("\\|");
        CommandType cmd = CommandType.fromString(parts[0]);   //El comando

        try {

            switch (cmd) {//TODO receive signals

                case SEND_SYMPTOMS:
                    return handleSendSymptoms(parts);

                case ADD_EXTRA_INFO:
                    return handleAddExtraInfo(parts);

                case GET_HISTORY:
                    return handleGetHistory(parts);

                case DISCONNECT:
                    return "OK|Disconnected";

                default:
                    return "ERROR|Unknown command " + cmd;
            }

        } catch (Exception ex) {
            // Si algo falla en cualquier comando → devolvemos error al cliente
            return "ERROR|" + ex.getMessage();
        }
    }

    private String handleSendSymptoms(String[] parts) {

        int clientId = Integer.parseInt(parts[1]);
        String symptomsCSV = parts[2];

        // Pasamos de string de sintomas con ',' a una lista de sintomas
        List<String> symptoms = new ArrayList<>(Arrays.asList(symptomsCSV.split(",")));

        // Creamos un objeto MedicalHistory SOLO para síntomas
        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setClientId(clientId);
        medicalHistory.setDate(LocalDate.now());
        medicalHistory.setSymptomsList(symptoms);

        // Guardamos en la BD usando el DAO
        //jdbcMedicalHistory.addSymptoms(medicalHistory); //TODO conexion base de datos

        return "OK|Symptoms saved";
    }

    private String handleAddExtraInfo(String[] parts) {

        int clientId = Integer.parseInt(parts[1]);
        double height = Double.parseDouble(parts[2]);
        double weight = Double.parseDouble(parts[3]);

        //jdbcClient.updateHeightWeight(clientId, height, weight);//TODO conexion base de datos

        return "OK|Extra info saved";
    }

    //Por cada GetHistory: DATE, SYMPTOMS, OBS
    private String handleGetHistory(String[] parts) {
        int clientId = Integer.parseInt(parts[1]);
        List<MedicalHistory> list = jdbcMedicalHistory.getMedicalHistoryByClientId(clientId);

        if (list.isEmpty()) {
            return "ERROR|No history";
        }

        String response = "HISTORY|";

        for (MedicalHistory mh : list) {

            response += "DATE: " + mh.getDate() + "\n";

            if (mh.getSymptomsList() != null)
                response += "SYMPTOMS: " + mh.getSymptomsList() + "\n";

            if (mh.getObservations() != null)
                response += "OBS: " + mh.getObservations() + "\n";

            response += "---------------------\n";
        }

        return response;

    }
}


