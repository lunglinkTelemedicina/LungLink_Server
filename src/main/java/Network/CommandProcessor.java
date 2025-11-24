package Network;

import Network.data.ReceiveDataViaNetwork;
import Network.data.SendDataViaNetwork;
import jdbc.*;
import pojos.*;
import utils.SecurityUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;


public class CommandProcessor {

    //These classes access the database
    private final JDBCClient jdbcClient;
    private final JDBCMedicalHistory jdbcMedicalHistory;
    private final JDBCSignal jdbcSignal;
    private final JDBCDoctor jdbcDoctor;

    public CommandProcessor() {
        this.jdbcClient = new JDBCClient();
        this.jdbcMedicalHistory = new JDBCMedicalHistory();
        this.jdbcSignal = new JDBCSignal();
        this.jdbcDoctor = new JDBCDoctor();
    }

    //This method gets a message and, depending on how it starts, calls one method or another to handle the request.
    public String handleClientRequest(String message, ReceiveDataViaNetwork receive, SendDataViaNetwork send) {

        //Empty message
        if (message == null || message.isEmpty())
            return "ERROR|Empty command";

        //We split the message: CMD|param1|param2|...
        String[] parts = message.split("\\|");
        CommandType cmd = CommandType.fromString(parts[0]);   //El comando

        try {

            switch (cmd) {

                case LOGIN_USER:
                    return handleLoginUser(parts);

                case REGISTER_USER:
                    return handleRegisterUser(parts);

                case CHECK_CLIENT:
                    return handleCheckClient(parts);

                case CREATE_CLIENT:
                    return handleCreateClient(parts);

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
                case CREATE_DOCTOR:
                    return handleCreateDoctor(parts);
                case CHECK_DOCTOR:
                    return handleCheckDoctor(parts);
                case GET_DOCTOR_PATIENTS:
                    return handleGetDoctorPatients(parts);
                case GET_PATIENT_HISTORY_DOCTOR:
                    return handleGetPatientHistoryDoctor(parts);
                case GET_PATIENT_SIGNALS:
                    return handleGetPatientsSignals(parts);
                case ADD_OBSERVATIONS:
                    return handleAddObservations(parts);


                default:
                    return "ERROR|Unknown command" + cmd;
            }

        } catch (Exception ex) {
            //If something fails in any command, we send an error back to the client
            return "ERROR|" + ex.getMessage();
        }
    }

    private String handleSendSymptoms(String[] parts) {

        int clientId;
        if (parts.length < 3) {
            throw new IllegalArgumentException("Missing arguments for SEND_SYMPTOMS (expected: CLIENT_ID|SYMPTOMS_CSV).");
        }

        try {
            clientId = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Client ID format.", e);
        }
        String symptomsCSV = parts[2];

        //We change the symptoms string with ',' into a list of symptoms
        List<String> symptoms = new ArrayList<>(Arrays.asList(symptomsCSV.split(",")));

        //We create a MedicalHistory object only for the symptoms
        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setClientId(clientId);
        medicalHistory.setDate(LocalDate.now());

        int recordId = jdbcMedicalHistory.addMedicalHistory(medicalHistory);

        if(recordId>0) {
            jdbcMedicalHistory.addSymptoms(recordId, symptoms);
            return "OK|Symptoms are saved";
        }

        return "ERROR|Failed to create medical history record in database.";    }

    private String handleAddExtraInfo(String[] parts) {

        int clientId;
        double height ;
        double weight;
        if (parts.length < 4) {
            throw new IllegalArgumentException("Missing arguments for ADD_EXTRA_INFO (expected: CLIENT_ID|HEIGHT|WEIGHT).");
        }

        try {
            clientId = Integer.parseInt(parts[1]);
            height = Double.parseDouble(parts[2]);
            weight = Double.parseDouble(parts[3]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format for Client ID, height, or weight.", e);
        }

        jdbcClient.updateHeightWeight(clientId, height, weight);
        return "OK|Extra info saved";
    }


    private String handleGetHistory(String[] parts) {
        int clientId;

        if (parts.length < 2) {
            throw new IllegalArgumentException("Missing Client ID for GET_HISTORY.");
        }

        try {
            clientId = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Client ID format.", e);
        }
        List<MedicalHistory> list = jdbcMedicalHistory.getMedicalHistoryByClientId(clientId);

        if (list.isEmpty() ||list==null) {
            return "ERROR|No history found";
        }

        String response = "";

        for (MedicalHistory mh : list) {

            response += "DATE: " + mh.getDate() + "\n";

            if (mh.getSymptomsList() != null)
                response += "SYMPTOMS: " + mh.getSymptomsList() + "\n";

            if (mh.getObservations() != null)
                response += "OBS: " + mh.getObservations() + "\n";

        }

        return response;

    }

    private String handleSignals(String[] parts, CommandType cmd, ReceiveDataViaNetwork receive, SendDataViaNetwork send) throws Exception {

        int clientId = Integer.parseInt(parts[1]);
        int numSamples = Integer.parseInt(parts[2]);

        TypeSignal type = (cmd == CommandType.SEND_ECG)
                ? TypeSignal.ECG
                : TypeSignal.EMG;

        // tell client to send bytes
        send.sendString("Client can send the data");

        // receive raw bytes
        byte[] raw = receive.receiveBytes();

        Signal signal = new Signal(type);
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

    private String handleRegisterUser(String[] parts) {
        if (parts.length < 3) {
            throw new IllegalArgumentException("Missing username or password for REGISTER_USER.");
        }
        String username = parts[1];
        String passwordPlain = parts[2];

        String passwordHash = SecurityUtils.hashPassword(passwordPlain);


        User user = new User(username, passwordHash);
        user.setUsername(username);

        JDBCUser jdbcUser = new JDBCUser();
        int newId = jdbcUser.addUser(user);

        if (newId > 0) {
            return "OK|" + newId;
        }

        return "ERROR|User registration failed";
    }

    private String handleLoginUser(String[] parts) {
        if (parts.length < 3) {
            throw new IllegalArgumentException("Missing username or password for LOGIN_USER.");
        }
        String username = parts[1];
        String passwordPlain = parts[2];

        String passwordHash = SecurityUtils.hashPassword(passwordPlain);

        JDBCUser jdbcUser = new JDBCUser();
        User user = jdbcUser.validateLogin(username, passwordHash);

        if (user == null) {
            return "ERROR|Invalid credentials";
        }

        return "OK|" + user.getId() + "|" + user.getUsername();
    }

    private String handleCheckClient(String[] parts) {

        int userId;
        if (parts.length < 2) {
            throw new IllegalArgumentException("Missing User ID for CHECK_CLIENT.");
        }

        try {
            userId = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid User ID format.", e);
        }
        JDBCClient jdbcClient = new JDBCClient();
        Client c = jdbcClient.getClientByUserId(userId);

        if (c == null) {
            return "OK|" + userId + "|NO_CLIENT";
        }

        String dob = c.getDob().getDayOfMonth() + "-" +
                c.getDob().getMonthValue() + "-" +
                c.getDob().getYear();

        return "OK|" +
                c.getUserId() + "|" +
                c.getClientId() + "|" +
                c.getName() + "|" +
                c.getSurname() + "|" +
                dob + "|" +
                c.getSex().name() + "|" +
                c.getMail();
    }

    private String handleCreateClient(String[] parts) {

        int userId, day, month, year;
        if (parts.length < 7) {
            throw new IllegalArgumentException("Missing arguments for CREATE_CLIENT (expected: USER_ID|NAME|SURNAME|D-M-Y|SEX|MAIL).");
        }
        String name = parts[2];
        String surname = parts[3];
        String sex = parts[5];
        String mail = parts[6];

        try{
            userId = Integer.parseInt(parts[4]);
            day = Integer.parseInt(parts[5]);
            month = Integer.parseInt(parts[6]);
            year = Integer.parseInt(parts[7]);
            String[] dobStr = parts[4].split("-");
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Invalid ID or DOB format. Expected: USER_ID|NAME|SURNAME|D-M-Y|SEX|MAIL", e);
        }

        LocalDate dobObject;
        try {
            dobObject = LocalDate.of(year, month, day);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format in DOB: " + parts[4], e);
        }

        try {
            // Valida si la cadena 'sex' es un valor válido de Enum
            Sex.valueOf(sex);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value for SEX: " + sex, e);
        }


        Client c = new Client();
        c.setUserId(userId);
        c.setName(name);
        c.setSurname(surname);
        c.setDob(LocalDate.of(year, month, day));
        c.setSex(Sex.valueOf(sex));
        c.setMail(mail);

        JDBCClient jdbcClient = new JDBCClient();
        int newId = jdbcClient.addClient(c);

        if (newId > 0) {
            return "OK|" + newId;
        }

        return "ERROR|Client creation failed";
    }

    private String handleCheckDoctor(String[] parts) {

        int userId = Integer.parseInt(parts[1]);

        Doctor d = jdbcDoctor.getDoctorByUserId(userId);

        if (d == null) {
            return "OK|" + userId + "|NO_DOCTOR";
        }

        return "OK|" + userId + "|DOCTOR|" +
                d.getDoctorId() + "|" +
                d.getName() + "|" +
                d.getSurname();
    }

    private String handleGetDoctorPatients(String[] parts) {

        int doctorId = Integer.parseInt(parts[1]);

        List<Client> list = jdbcDoctor.getClientsByDoctorId(doctorId);

        if (list.isEmpty()) {
            return "ERROR|No patients found";
        }

        StringBuilder sb = new StringBuilder("OK|");

        for (int i = 0; i < list.size(); i++) {
            Client c = list.get(i);

            String dobStr = c.getDob() != null ?
                    c.getDob().getDayOfMonth() + "-" +
                            c.getDob().getMonthValue() + "-" +
                            c.getDob().getYear() :
                    "";

            sb.append(c.getClientId()).append(";")
                    .append(c.getName()).append(";")
                    .append(c.getSurname()).append(";")
                    .append(dobStr).append(";")
                    .append(c.getSex() != null ? c.getSex().name() : "").append(";")
                    .append(c.getMail() != null ? c.getMail() : "");

            if (i < list.size() - 1) sb.append("#");
        }

        return sb.toString();
    }

    private String handleGetPatientHistoryDoctor(String[] parts) {

        int clientId = Integer.parseInt(parts[1]);
        List<MedicalHistory> list = jdbcMedicalHistory.getMedicalHistoryByClientId(clientId);

        if (list.isEmpty()) {
            return "ERROR|No history found";
        }

        StringBuilder response = new StringBuilder();

        for (MedicalHistory mh : list) {

            response.append("RECORD_ID: ").append(mh.getRecordId()).append("\n");
            response.append("DATE: ").append(mh.getDate()).append("\n");

            if (mh.getSymptomsList() != null) {
                response.append("SYMPTOMS: ")
                        .append(String.join(",", mh.getSymptomsList()))
                        .append("\n");
            }

            if (mh.getObservations() != null) {
                response.append("OBS: ").append(mh.getObservations()).append("\n");
            }

            response.append("\n");
        }

        return response.toString();
    }

    private String handleGetPatientsSignals(String[] parts) {

        int clientId = Integer.parseInt(parts[1]);

        List<Signal> signals = jdbcSignal.getSignalsByClientId(clientId);

        if (signals.isEmpty()) {
            return "ERROR|No signals found";
        }

        StringBuilder sb = new StringBuilder();

        for (Signal s : signals) {
            sb.append("SIGNAL_ID: ").append(s.getSignalId()).append("\n");
            sb.append("TYPE: ").append(s.getType() != null ? s.getType().name() : "").append("\n");
            sb.append("RECORD_ID: ").append(s.getRecordId()).append("\n");
            sb.append("FILE: ").append(s.getSignalFile() != null ? s.getSignalFile() : "").append("\n");
            sb.append("\n");
        }

        return sb.toString();
    }


    private String handleAddObservations(String[] parts) {

        int recordId;
        if (parts.length < 3) {
            throw new IllegalArgumentException("Missing arguments for ADD_OBSERVATIONS (expected: RECORD_ID|NOTE).");
        }

        try {
            recordId = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Record ID format.", e);
        }
        String note = parts[2];

        jdbcMedicalHistory.updateObservations(recordId, note);

        return "OK|Observation added";
    }

    private String handleCreateDoctor(String[] parts) {

        int userId;
        if (parts.length < 6) {
            throw new IllegalArgumentException("Missing arguments for CREATE_DOCTOR (expected: USER_ID|NAME|SURNAME|SPECIALTY|EMAIL).");
        }

        try {
            userId = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid User ID format.", e);
        }
        String name = parts[2];
        String surname = parts[3];
        String specialty = parts[4];
        String email = parts[5];

        Doctor existing = jdbcDoctor.getDoctorByUserId(userId);
        if (existing != null) {
            return "OK|Doctor Already Exists";
        }

        Doctor d = new Doctor();
        d.setUserId(userId);
        d.setName(name);
        d.setSurname(surname);
        d.setEmail(email);

        try {
            d.setSpecialty(DoctorSpecialty.valueOf(specialty));
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid value for Doctor Specialty."+ specialty, e);
        }
        jdbcDoctor.addDoctor(d);

        return "OK|DoctorCreated";
    }






}




