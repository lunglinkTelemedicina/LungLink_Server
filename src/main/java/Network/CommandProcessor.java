package Network;
//TODO probar que funcione bien

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
    public String process(String msg) {

        //Mensaje vacio
        if (msg == null || msg.isEmpty())
            return "ERROR|Empty command";

        // Dividimos el comando: CMD|param1|param2|...
        String[] parts = msg.split("\\|");
        CommandType cmd = CommandType.fromString(parts[0]);   //El comando

        try {

            switch (cmd) {

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

        // Convertimos CSV → Lista<String>
        List<String> symptoms = new ArrayList<>(Arrays.asList(symptomsCSV.split(",")));

        // Creamos un objeto MedicalHistory SOLO para síntomas
        MedicalHistory mh = new MedicalHistory();
        mh.setClientId(clientId);
        mh.setDate(LocalDate.now());
        mh.setSymptomsList(symptoms);

        // Guardamos en la BD usando el DAO
        //historyDAO.addSymptoms(mh); //TODO habria que crear addSymptons en la base de datos

        return "OK|Symptoms saved";
    }

    private String handleAddExtraInfo(String[] parts) {

        int clientId = Integer.parseInt(parts[1]);
        double height = Double.parseDouble(parts[2]);
        double weight = Double.parseDouble(parts[3]);

        //clientDAO.updateHeightWeight(clientId, height, weight);//TODO

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

    //TODO queremos que el paciente vea sus señales?
    //TODO ademas este metodo no estaria enseñando las señales habria que revisarlo
//    private String handleGetSignals(String[] parts) {
//        String type = parts[1];       // ECG / EMG / ALL
//        int clientId = Integer.parseInt(parts[2]);
//
//        List<Signal> signals = signalDAO.getSignalsByClient(clientId);
//
//        if (signals.isEmpty()) {
//            return "ERROR|No signals found";
//        }
//
//        String response = "SIGNALS|";
//
//        for (Signal signal : signals) {
//
//            if (type.equals("ALL") || signal.getType().name().equals(type)) {
//                response = response + signal.getType() + ": " + signal.valuesToDB() + "\n";
//            }
//        }
//
//        return response;
//
//    }
}


