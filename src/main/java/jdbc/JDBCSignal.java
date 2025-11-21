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
            INSERT INTO signal (type, signal_file, client_id)
            VALUES (?, ?, ?)
        """;

        int generatedId = -1;
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, signal.getType() != null ? signal.getType().name() : null);
            ps.setString(2, signal.getSignalFile());
            ps.setInt(3, signal.getClientId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    System.out.println("Signal correctly inserted with ID: " + generatedId);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting señal:");
            e.printStackTrace();
        }

    }

    @Override
    public Signal getSignalById(int signalId) {
        String sql = "SELECT * FROM signal WHERE signal_id = ?";
        Signal s = null;

        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, signalId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                TypeSignal type = null;
                String typeString = rs.getString("type");
                if (typeString != null) {
                    type = TypeSignal.valueOf(typeString);
                }

                int clientId = rs.getInt("client_id");
                s = new Signal(type, clientId);
                s.setSignalFile(rs.getString("signal_file"));
            }

        } catch (SQLException e) {
            System.err.println("Error getting signal by ID:");
            e.printStackTrace();
        }

        return s;
    }

    @Override
    public List<Signal> getSignalsByClient(int clientId) {
        List<Signal> list = new ArrayList<>();
        String sql = "SELECT * FROM signal WHERE client_id = ?";

        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                TypeSignal type = null;
                String typeString = rs.getString("type");
                if (typeString != null) {
                    type = TypeSignal.valueOf(typeString);
                }

                Signal s = new Signal(type, clientId);
                s.setSignalFile(rs.getString("signal_file"));

                list.add(s);
            }

        } catch (SQLException e) {
            System.err.println("Error getting signals by client:");
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void deleteSignal(int signalId) {
        String sql = "DELETE FROM signal WHERE signal_id = ?";

        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, signalId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Signal correctly deleted (ID " + signalId + ")");
            } else {
                System.out.println("Signal not found");
            }

        } catch (SQLException e) {
            System.err.println("Error deleting signal:");
            e.printStackTrace();
        }
    }
}

