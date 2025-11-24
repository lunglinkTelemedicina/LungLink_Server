package jdbc;

import jdbcInterfaces.SignalManager;
import pojos.Signal;
import pojos.TypeSignal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCSignal implements SignalManager {



    @Override
    public void addSignal(Signal signal) {
        String sql = """
            INSERT INTO signal (type, signal_file, record_id)
            VALUES (?, ?, ?)
        """;


        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, signal.getType() != null ? signal.getType().name() : null);
            ps.setString(2, signal.getSignalFile());
            ps.setInt(3, signal.getRecordId());

            ps.executeUpdate();

            System.out.println("Signal correctly inserted.");

        } catch (SQLException e) {
            System.err.println("Error inserting signal:");
            e.printStackTrace();
        }
    }

    @Override
    public Signal getSignalById(int signalId) {
        String sql = "SELECT * FROM signal WHERE signal_id = ?";
        Signal s = null;


        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, signalId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                s = new Signal();

                String typeString = rs.getString("type");
                if (typeString != null)
                    s.setType(TypeSignal.valueOf(typeString.toUpperCase()));

                s.setSignalFile(rs.getString("signal_file"));

                s.setRecordId(rs.getInt("record_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting signal by ID:");
            e.printStackTrace();
        }

        return s;
    }

    @Override
    public List<Signal> getSignalsByRecordId(int recordId) {
        List<Signal> list = new ArrayList<>();
        String sql = "SELECT * FROM signal WHERE record_id = ?";


        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recordId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Signal s = new Signal();

                String typeString = rs.getString("type");
                if (typeString != null)
                    s.setType(TypeSignal.valueOf(typeString.toUpperCase()));

                s.setSignalFile(rs.getString("signal_file"));
                s.setRecordId(recordId);

                list.add(s);
            }

        } catch (SQLException e) {
            System.err.println("Error getting signals by record:");
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void deleteSignal(int signalId) {
        String sql = "DELETE FROM signal WHERE signal_id = ?";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, signalId);
            ps.executeUpdate();

            System.out.println("Signal deleted.");

        } catch (SQLException e) {
            System.err.println("Error deleting signal:");
            e.printStackTrace();
        }
    }

    public List<Signal> getSignalsByClientId(int clientId) {

        List<Signal> list = new ArrayList<>();

        String sql = """
        SELECT s.signal_id, s.type, s.signal_file, s.record_id
        FROM signal s
        JOIN medicalhistory m ON s.record_id = m.record_id
        WHERE m.client_id = ?
    """;

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Signal s = new Signal();
                s.setSignalId(rs.getInt("signal_id"));
                s.setRecordId(rs.getInt("record_id"));
                s.setSignalFile(rs.getString("signal_file"));

                String typeStr = rs.getString("type");
                if (typeStr != null) {
                    s.setType(TypeSignal.valueOf(typeStr));
                }

                list.add(s);
            }

        } catch (SQLException e) {
            System.err.println("Error when obtaining signals by client id");
            e.printStackTrace();
        }

        return list;
    }

}


