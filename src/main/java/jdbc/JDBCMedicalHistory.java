package jdbc;

import jdbcInterfaces.MedicalHistoryManager;
import pojos.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class JDBCMedicalHistory implements MedicalHistoryManager {


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

            // observations
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

    @Override
    public MedicalHistory getMedicalHistoryById(int recordId) {

        String sql = "SELECT * FROM medicalhistory WHERE record_id = ?";
        MedicalHistory mh = null;

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recordId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                mh = new MedicalHistory();
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
            }

        } catch (SQLException e) {
            System.err.println("Error when obtaining medical history by id:");
            e.printStackTrace();
       }

        return mh;
    }

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

    @Override
    public List<MedicalHistory> getMedicalHistories() {

        List<MedicalHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM medicalhistory";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

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

                list.add(mh);
            }

        } catch (SQLException e) {
            System.err.println("Error when obtaining medical histories ");
            e.printStackTrace();
        }

        return list;
    }

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

            // SIEMPRE asignar doctor general para síntomas
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


    @Override
    public void deleteMedicalHistory(int recordId) {

        String sql = "DELETE FROM medicalhistory WHERE record_id = ?";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recordId);
            ps.executeUpdate();

            System.out.println("Medical History " + recordId + "correctly deleted ");

        } catch (SQLException e) {
            System.err.println("Error when deleting medical history");
            e.printStackTrace();
        }
    }


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

    public void assignPendingRecordsToDoctor(int doctorId, DoctorSpecialty specialty) {

        String selectRecords = """
            SELECT m.record_id
            FROM medicalhistory m
            LEFT JOIN signal s ON m.record_id = s.record_id
            WHERE m.doctor_id IS NULL
        """;

        //chooses specialty
        switch (specialty) {
            case CARDIOLOGIST ->
                    selectRecords += " AND s.type = 'ECG'";

            case NEUROPHYSIOLOGIST ->
                    selectRecords += " AND s.type = 'EMG'";

            case GENERAL_MEDICINE ->
                    selectRecords += " AND s.type IS NULL";
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
