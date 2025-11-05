package jdbc;

import jdbcInterfaces.MedicalHistoryManager;
import pojos.MedicalHistory;

import db.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;


public class jdbcMedicalHistory implements MedicalHistoryManager {
    @Override
    public int addMedicalHistory(MedicalHistory m) {

        String sql = "INSERT INTO medical_history(date, client_id, doctor_id, observations) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getDate().toString());
            ps.setInt(2, m.getClientId());
            ps.setInt(3, m.getDoctorId());
            ps.setString(4, m.getObservations());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public MedicalHistory getMedicalHistoryById(int recordId) {

        String sql = "SELECT * FROM medical_history WHERE record_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recordId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                MedicalHistory mh = new MedicalHistory();
                mh.setRecordId(rs.getInt("record_id"));
                mh.setDate(LocalDate.parse(rs.getString("date")));
                mh.setClientId(rs.getInt("client_id"));
                mh.setDoctorId(rs.getInt("doctor_id"));
                mh.setObservations(rs.getString("observations"));
                return mh;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<MedicalHistory> getMedicalHistoryByClientId(int clientId) {
        List<MedicalHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM medical_history WHERE client_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MedicalHistory mh = new MedicalHistory();
                mh.setRecordId(rs.getInt("record_id"));
                mh.setDate(LocalDate.parse(rs.getString("date")));
                mh.setClientId(rs.getInt("client_id"));
                mh.setDoctorId(rs.getInt("doctor_id"));
                mh.setObservations(rs.getString("observations"));
                list.add(mh);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<MedicalHistory> getMedicalHistories() {
        return List.of();
    }

    @Override
    public void deleteMedicalHistory(int recordId) {

        String sql = "DELETE FROM medical_history WHERE record_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recordId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

