package Network;

import Network.data.ReceiveDataViaNetwork;
import Network.data.SendDataViaNetwork;

import jdbc.*;

import pojos.*;
import utils.SecurityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;


public class CommandProcessor {

    //These classes access the database
    private final JDBCClient jdbcClient;
    private final JDBCMedicalHistory jdbcMedicalHistory;
    private final JDBCSignal jdbcSignal;
    private final JDBCDoctor jdbcDoctor;
    private final DoctorAssignmentService doctorAssignmentService;

    public CommandProcessor(DoctorAssignmentService doctorAssignmentService) {
        this.jdbcClient = new JDBCClient();
        this.jdbcMedicalHistory = new JDBCMedicalHistory();
        this.jdbcSignal = new JDBCSignal();
        this.jdbcDoctor = new JDBCDoctor();
        this.doctorAssignmentService = doctorAssignmentService;
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
                case GET_PATIENT_SIGNALS_DOCTOR:
                    return handleGetPatientSignalsDoctor(parts);
                case ADD_OBSERVATIONS:
                    return handleAddObservations(parts);
                case GET_SIGNAL_FILE:
                    return handleGetSignalFile(parts, send);


                default:
                    return "ERROR|Unknown command" + cmd;
            }

        } catch (Exception ex) {
            //If something fails in any command, we send an error back to the client
            return "ERROR|" + ex.getMessage();
        }
    }

    private String handleSendSymptoms(String[] parts) {

        int clientId = Integer.parseInt(parts[1]);
        String symptomsCSV = parts[2];

        //We change the symptoms string with ',' into a list of symptoms
        List<String> symptoms = new ArrayList<>(Arrays.asList(symptomsCSV.split(",")));

        //We create a MedicalHistory object only for the symptoms
        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setClientId(clientId);
        medicalHistory.setDate(LocalDate.now());

        int recordId = jdbcMedicalHistory.addMedicalHistory(medicalHistory);
        jdbcMedicalHistory.addSymptoms(recordId, symptoms);

        int defaultDoctorId = jdbcDoctor.getDefaultDoctorId();
        jdbcClient.updateDoctorForClient(clientId, defaultDoctorId);

        Doctor defaultDoctor = jdbcDoctor.getDefaultDoctor();
        return "OK|Symptoms are saved|AssignedDoctor=" + defaultDoctor.getName() + ";" + defaultDoctor.getSpecialty();
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
            return "ERROR|No history found";
        }
        StringBuilder response = new StringBuilder();
        for (MedicalHistory mh : list) {

            response.append("RECORD ID: ").append(mh.getRecordId()).append("\n");
            response.append("DATE: ").append(mh.getDate()).append("\n");

            // Obtener si este record tiene señal asociada (ECG/EMG)
            TypeSignal type = jdbcSignal.getSignalTypeByRecordId(mh.getRecordId());

            if (type == null) {
                // ES UN REGISTRO DE SÍNTOMAS
                response.append("TYPE: SYMPTOMS\n");
                if (mh.getSymptomsList() != null) {
                    response.append("SYMPTOMS: ")
                            .append(String.join(",", mh.getSymptomsList()))
                            .append("\n");
                }
            } else {
                // ES ECG o EMG
                response.append("TYPE: ").append(type.name()).append("\n");
                // Coger archivo de la señal
                List<Signal> signals = jdbcSignal.getSignalsByRecordId(mh.getRecordId());
                if (!signals.isEmpty()) {
                    response.append("FILE: ")
                            .append(signals.get(0).getSignalFile())
                            .append("\n");
                }
            }
            if (mh.getObservations() != null) {
                response.append("OBS: ").append(mh.getObservations()).append("\n");
            }
            response.append("\n");
        }
        return response.toString();
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
        send.sendString("OK|Client can send the data");

        // receive raw bytes
        byte[] raw = receive.receiveBytes();
        System.out.println("handleSignals → received " + raw.length + " raw bytes for " + type); //TODO NUEVO

        Signal signal = new Signal(type);
        signal.fromByteArray(raw);
        System.out.println("handleSignals → signal has " + signal.getValues().size() + " samples");

        Doctor assigned = doctorAssignmentService.getDoctorForSignal(type);

        if (assigned == null) {
            System.out.println("handleSignals → WARNING: No doctor available for " + type);
            return "ERROR|No doctor available for " + type.name();
        }

        // 5) Asignar el doctor al CLIENTE
        jdbcClient.updateDoctorForClient(clientId, assigned.getDoctorId());
        System.out.println("Doctor assigned: " + assigned.getName() + "(" + assigned.getSpecialty() + ")");

        MedicalHistory medicalHistory = new MedicalHistory();
        medicalHistory.setClientId(clientId);
        medicalHistory.setDate(LocalDate.now());

        int recordId = jdbcMedicalHistory.addMedicalHistory(medicalHistory);
        signal.setRecordId(recordId);

        String updateMH = "UPDATE medicalhistory SET doctor_id = ? WHERE record_id = ?";
        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(updateMH)) {

            ps.setInt(1, assigned.getDoctorId());
            ps.setInt(2, recordId);
            ps.executeUpdate();

            System.out.println("MedicalHistory " + recordId +
                    " updated with doctor_id=" + assigned.getDoctorId());
        }

        try {
            String fileName = signal.saveAsFile();
            signal.setSignalFile(fileName);

            System.out.println("handleSignals → signal file saved as " + fileName);

            jdbcSignal.addSignal(signal);
            System.out.println("handleSignals → signal stored in DB with recordId=" + recordId);

            return "OK|Signal saved|AssignedDoctor=" + assigned.getName() + ";" + assigned.getSpecialty();

        } catch (IOException e) {
            System.err.println("Error saving signal file: " + e.getMessage());
            return "ERROR|Signal could not be saved";
        }
    }

    private String handleRegisterUser(String[] parts) {

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

        int userId = Integer.parseInt(parts[1]);

        //comentar esto??
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

        int userId = Integer.parseInt(parts[1]);
        String name = parts[2];
        String surname = parts[3];
        String dobStr = parts[4];
        String sex = parts[5];
        String mail = parts[6];

        String[] dobParts = dobStr.split("-");
        int day = Integer.parseInt(dobParts[0]);
        int month = Integer.parseInt(dobParts[1]);
        int year = Integer.parseInt(dobParts[2]);

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

        int doctorId = Integer.parseInt(parts[1]);
        int clientId = Integer.parseInt(parts[2]);

        if(!jdbcDoctor.isPatientAssignedToDoctor(doctorId, clientId)){
            return "ERROR|Patients not assigned to this Doctor";
        }

        List<MedicalHistory> list = jdbcMedicalHistory.getMedicalHistoryByClientId(clientId);

        if (list.isEmpty()) {
            return "ERROR|No history found";
        }

        StringBuilder response = new StringBuilder();

        for (MedicalHistory mh : list) {

            response.append("RECORD_ID: ").append(mh.getRecordId()).append("\n");
            response.append("DATE: ").append(mh.getDate()).append("\n");

            TypeSignal type = jdbcSignal.getSignalTypeByRecordId(mh.getRecordId());

            if (type == null) {
                response.append("TYPE: SYMPTOMS\n");

                if (mh.getSymptomsList() != null) {
                    response.append("SYMPTOMS: ")
                            .append(String.join(",", mh.getSymptomsList()))
                            .append("\n");
                }
            } else {
                response.append("TYPE: ").append(type.name()).append("\n");

                List<Signal> signals = jdbcSignal.getSignalsByRecordId(mh.getRecordId());
                if (!signals.isEmpty()) {
                    response.append("FILE: ").append(signals.get(0).getSignalFile()).append("\n");
                }
            }
//            if (mh.getSymptomsList() != null) {
//                response.append("SYMPTOMS: ")
//                        .append(String.join(",", mh.getSymptomsList()))
//                        .append("\n");
//            }

            if (mh.getObservations() != null) {
                response.append("OBS: ").append(mh.getObservations()).append("\n");
            }

            response.append("\n");
        }

        return response.toString();
    }

    private String handleGetPatientSignalsDoctor(String[] parts) {

        int doctorId = Integer.parseInt(parts[1]);
        int clientId = Integer.parseInt(parts[2]);

        if (!jdbcDoctor.isPatientAssignedToDoctor(doctorId, clientId)) {
            return "ERROR|Patient not assigned to this Doctor";
        }

        List<Signal> signals = jdbcSignal.getSignalsByClientId(clientId);

        if (signals.isEmpty()) {
            return "ERROR|No signals found";
        }

        StringBuilder sb = new StringBuilder();

        for (Signal s : signals) {
            sb.append("SIGNAL_ID: ").append(s.getSignalId()).append("\n");
            sb.append("TYPE: ").append(s.getType() != null ? s.getType().name() : "").append("\n");
            //sb.append("RECORD_ID: ").append(s.getRecordId()).append("\n");
            sb.append("FILE: ").append(s.getSignalFile()).append("\n\n");
        }

        return sb.toString();
    }

    private String handleAddObservations(String[] parts) {

        int recordId = Integer.parseInt(parts[1]);
        String note = parts[2];

        jdbcMedicalHistory.updateObservations(recordId, note);

        return "OK|Observation added";
    }

    private String handleCreateDoctor(String[] parts) {

        int userId = Integer.parseInt(parts[1]);
        String name = parts[2];
        String surname = parts[3];
        String specialty = parts[4];
        String email = parts[5];

        // Check if doctor already exists
        Doctor existing = jdbcDoctor.getDoctorByUserId(userId);
        if (existing != null) {
            return "OK|" + existing.getDoctorId();
        }

        Doctor d = new Doctor();
        d.setUserId(userId);
        d.setName(name);
        d.setSurname(surname);
        d.setEmail(email);
        d.setSpecialty(DoctorSpecialty.valueOf(specialty));

        jdbcDoctor.addDoctor(d);

        Doctor created = jdbcDoctor.getDoctorByUserId(userId);
        if (created == null) {
            return "ERROR|DoctorNotCreated";
        }

        // RETURN FORMAT:  OK|doctorId
        return "OK|" + created.getDoctorId();
    }

    private String handleGetSignalFile(String[] parts, SendDataViaNetwork send) {
        try {
            int signalId = Integer.parseInt(parts[1]);

            Signal s = jdbcSignal.getSignalById(signalId);
            if (s == null) {
                return "ERROR|Signal not found";
            }

            int clientOwner = jdbcSignal.getClientIdBySignalId(signalId);

            if (clientOwner != Integer.parseInt(parts[2])) {
                return "ERROR|Signal does not belong to this patient";
            }

            File file = new File("signals/" + s.getSignalFile());
            if (!file.exists()) {
                System.out.println("File  not found: " + file.getAbsolutePath());
                return "ERROR|File not found on server";
            }

            byte[] data = Files.readAllBytes(file.toPath());

            send.sendString("OK|SENDING_FILE|" + data.length);
            send.sendRawBytes(data);
           // send.sendBytes(data);

            return "NO_REPLY";

        } catch (Exception e) {
            return "ERROR|" + e.getMessage();
        }
    }

}
