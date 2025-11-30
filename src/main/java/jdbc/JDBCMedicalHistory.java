package jdbc;

import jdbcInterfaces.MedicalHistoryManager;
import pojos.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
/**
 * Implementation of the {@code MedicalHistoryManager} interface using JDBC
 * for managing persistence operations related to medical history records in the database.
 * * This class handles creating, retrieving, and updating medical history entries,
 * as well as managing the assignment of pending records to doctors based on signal type.
 */
public class JDBCMedicalHistory implements MedicalHistoryManager {
    /**
     * Inserts a new medical history record into the 'medicalhistory' table.
     * * @param m The MedicalHistory object to insert. It must contain the client ID, date,
     * and observations. The doctor ID is optional.
     * @return The automatically generated record ID for the new medical history, or -1 if an error occurred.
     */
    @Override
    public int addMedicalHistory(MedicalHistory m) {

        String sql = """
            INSERT INTO medicalhistory (date, client_id, doctor_id, observations)
            VALUES (?, ?, ?, ?)
        """;

        int generatedId = -1;

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getDate() != null ? m.getDate().toString() : null);

            ps.setInt(2, m.getClientId());

            if (m.getDoctorId() > 0) {
                ps.setInt(3, m.getDoctorId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setString(4, m.getObservations());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    m.setRecordId(generatedId);
                }
            }

            System.out.println("Medical History inserted with ID: " + generatedId + "\n");

        } catch (SQLException e) {
            System.err.println("Error when inserting medical history: ");
            e.printStackTrace();
        }

        return generatedId;
    }
    /**
     * Retrieves all medical history records associated with a specific client ID.
     * * @param clientId The ID of the client whose history records are to be retrieved.
     * @return A list of {@code MedicalHistory} objects for the given client. Returns an empty list if none are found.
     */
    @Override
    public List<MedicalHistory> getMedicalHistoryByClientId(int clientId) {

        List<MedicalHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM medicalhistory WHERE client_id = ?";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MedicalHistory mh = new MedicalHistory();
                mh.setRecordId(rs.getInt("record_id"));

                String dateStr = rs.getString("date");
                if (dateStr != null) {
                    mh.setDate(LocalDate.parse(dateStr));
                }

                mh.setClientId(rs.getInt("client_id"));
                mh.setDoctorId(rs.getInt("doctor_id"));
                mh.setObservations(rs.getString("observations"));

                String symptomsStr = rs.getString("symptomsList");
                if (symptomsStr != null && !symptomsStr.isEmpty()) {
                    mh.setSymptomsList(Arrays.asList(symptomsStr.split(",")));
                }

                list.add(mh);
            }

        } catch (SQLException e) {
            System.err.println("Error when obtaining medical histories by client");
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Adds a list of symptoms to an existing medical history record.
     * <p>After successfully adding symptoms, this method assigns the default doctor
     * (retrieved from {@code JDBCDoctor.getDefaultDoctorId()}) to the medical history record.</p>
     * * @param recordId The ID of the medical history record to update.
     * @param symptomsList The list of symptoms (Strings) to add.
     */
    public void addSymptoms(int recordId, List<String> symptomsList) {

        if (symptomsList == null || symptomsList.isEmpty()) {
            System.out.println("There are no symptoms to add.");
            return;
        }

        String symptomsStr = String.join(",", symptomsList);

        String sql = "UPDATE medicalhistory SET symptomsList = ? WHERE record_id = ?";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, symptomsStr);
            ps.setInt(2, recordId);
            ps.executeUpdate();

            System.out.println("Symptoms correctly added to medical history " + recordId);

            // default doctor assigned when patient only has symptoms
            int defaultDoctorId = JDBCDoctor.getInstance().getDefaultDoctorId();

            String updateMH = "UPDATE medicalhistory SET doctor_id = ? WHERE record_id = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(updateMH)) {
                ps2.setInt(1, defaultDoctorId);
                ps2.setInt(2, recordId);
                ps2.executeUpdate();
            }

            System.out.println("General doctor assigned to medical history " + recordId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Updates the observations field for an existing medical history record.
     * * @param recordId The ID of the medical history record to update.
     * @param observations The new observation text to set.
     */
    public void updateObservations(int recordId, String observations) {

        String sql = "UPDATE medicalhistory SET observations = ? WHERE record_id = ?";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, observations);
            ps.setInt(2, recordId);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error when updating observations");
            e.printStackTrace();
        }
    }
    /**
     * Assigns pending medical history records to a specific doctor based on the doctor's specialty
     * and the type of signal (ECG or EMG) associated with the record.
     * * <p>A record is considered "pending" if its {@code doctor_id} is NULL.</p>
     * * <ul>
     * <li>**CARDIOLOGIST:** Assigns records that have an 'ECG' signal.</li>
     * <li>**NEUROPHYSIOLOGIST:** Assigns records that have an 'EMG' signal.</li>
     * <li>**GENERAL_MEDICINE:** Assigns records that have NO signal attached (s.type IS NULL).</li>
     * </ul>
     * * @param doctorId The ID of the doctor to assign the records to.
     * @param specialty The specialty of the doctor, used to filter which pending records to assign.
     */
    public void assignPendingRecordsToDoctor(int doctorId, DoctorSpecialty specialty) {

        String selectRecords = """
            SELECT m.record_id
            FROM medicalhistory m
            LEFT JOIN signal s ON m.record_id = s.record_id
            WHERE m.doctor_id IS NULL
        """;

        switch (specialty) {

            case CARDIOLOGIST:
                selectRecords += " AND s.type = 'ECG'";
                break;

            case NEUROPHYSIOLOGIST:
                selectRecords += " AND s.type = 'EMG'";
                break;

            case GENERAL_MEDICINE:
                selectRecords += " AND s.type IS NULL";
                break;
        }

        List<Integer> pendingRecords = new ArrayList<>();

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(selectRecords)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                pendingRecords.add(rs.getInt(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (pendingRecords.isEmpty()) {
            System.out.println("No pending records for specialty " + specialty);
            return;
        }

        String updateMH = """
            UPDATE medicalhistory
            SET doctor_id = ?
            WHERE record_id = ?
        """;

        String updateClient = """
            UPDATE client
            SET doctor_id = ?
            WHERE client_id = (
                SELECT client_id FROM medicalhistory WHERE record_id = ?
            )
        """;

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement psMH = conn.prepareStatement(updateMH);
             PreparedStatement psC = conn.prepareStatement(updateClient)) {

            conn.setAutoCommit(false);

            for (int recordId : pendingRecords) {

                psMH.setInt(1, doctorId);
                psMH.setInt(2, recordId);
                psMH.executeUpdate();

                psC.setInt(1, doctorId);
                psC.setInt(2, recordId);
                psC.executeUpdate();
            }

            conn.commit();

            System.out.println("Assigned " + pendingRecords.size() +
                    " records + clients to doctor " + doctorId +
                    " (" + specialty + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
