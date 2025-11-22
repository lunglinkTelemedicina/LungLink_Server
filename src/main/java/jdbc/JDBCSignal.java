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
        JDBCConnectionManager cm = new  JDBCConnectionManager();
        String sql = """
            INSERT INTO signal (type, signal_values, signal_file, sampling_rate, record_id)
            VALUES (?, ?, ?, ?, ?)
        """;


        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, signal.getType() != null ? signal.getType().name() : null);
            ps.setString(2, signal.valuesToDB());
            ps.setString(3, signal.getSignalFile());
            ps.setInt(4, signal.getSamplingRate());
            ps.setInt(5, signal.getRecordId());

            ps.executeUpdate();

            System.out.println("Signal correctly inserted.");

        } catch (SQLException e) {
            System.err.println("Error inserting signal:");
            e.printStackTrace();
        }
    }

    @Override
    public Signal getSignalById(int signalId) {
        JDBCConnectionManager cm = new  JDBCConnectionManager();
        String sql = "SELECT * FROM signal WHERE signal_id = ?";
        Signal s = null;


        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, signalId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                s = new Signal();

                String typeString = rs.getString("type");
                if (typeString != null)
                    s.setType(TypeSignal.valueOf(typeString.toUpperCase()));

                String valuesString = rs.getString("signal_values");
                if (valuesString != null)
                    s.valuesToDB();

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
        JDBCConnectionManager cm = new  JDBCConnectionManager();
        List<Signal> list = new ArrayList<>();
        String sql = "SELECT * FROM signal WHERE record_id = ?";


        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recordId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Signal s = new Signal();

                String typeString = rs.getString("type");
                if (typeString != null)
                    s.setType(TypeSignal.valueOf(typeString.toUpperCase()));

                String valuesString = rs.getString("signal_values");
                if (valuesString != null)
                    s.valuesToList(valuesString);

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
        JDBCConnectionManager cm = new  JDBCConnectionManager();
        String sql = "DELETE FROM signal WHERE signal_id = ?";

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, signalId);
            ps.executeUpdate();

            System.out.println("Signal deleted.");

        } catch (SQLException e) {
            System.err.println("Error deleting signal:");
            e.printStackTrace();
        }
    }
}


