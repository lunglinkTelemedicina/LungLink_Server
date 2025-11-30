package jdbc;

import jdbcInterfaces.SignalManager;
import pojos.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Implementation of the {@code SignalManager} interface using JDBC
 * for managing persistence operations related to signal records in the database.
 * This class handles CRUD operations for the 'signal' table and complex queries
 * involving joins with 'medicalhistory' and 'client'.
 */
public class JDBCSignal implements SignalManager {
    /**
     * Inserts a new signal record into the 'signal' table.
     *
     * @param signal The Signal object to insert, containing the type, file path, and associated record ID.
     */
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
    /**
     * Retrieves a specific Signal record from the database using its unique signal ID.
     *
     * @param signalId The unique ID of the signal to retrieve.
     * @return The populated Signal object, or null if the signal is not found or an error occurs.
     */
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

    /**
     * Retrieves all signal records associated with a specific medical history record ID.
     *
     * @param recordId The ID of the medical history record whose signals are to be retrieved.
     * @return A list of {@code Signal} objects associated with the record. Returns an empty list if none are found.
     */
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

    /**
     * Retrieves a list of all signal records associated with a specific client ID.
     * <p>This operation involves a JOIN between the 'signal' and 'medicalhistory' tables
     * to filter by the client's ID.</p>
     *
     * @param clientId The ID of the client whose signals are to be retrieved.
     * @return A list of {@code Signal} objects associated with the client.
     */
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
    /**
     * Retrieves the type (ECG or EMG) of the signal associated with a given medical history record ID.
     * <p>Assumes a medical history record generally has only one type of signal for determination.</p>
     *
     * @param recordId The ID of the medical history record.
     * @return The {@code TypeSignal} enum value (ECG or EMG), or null if no signal is found for the record.
     */
    public TypeSignal getSignalTypeByRecordId(int recordId) {
        String sql = "SELECT type FROM signal WHERE record_id = ? LIMIT 1";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, recordId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String typeStr = rs.getString("type");
                if (typeStr != null) {
                    return TypeSignal.valueOf(typeStr.toUpperCase());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    /**
     * Retrieves the client ID associated with a specific signal ID.
     * <p>This requires joining the 'signal', 'medicalhistory', and 'client' tables.</p>
     *
     * @param signalId The ID of the signal whose owner is to be determined.
     * @return The ID of the client who owns the signal, or -1 if the signal or client association is not found.
     */
    public int getClientIdBySignalId(int signalId) {

        String sql = """
        SELECT client.client_id
        FROM signal
        JOIN medicalhistory ON signal.record_id = medicalhistory.record_id
        JOIN client ON medicalhistory.client_id = client.client_id
        WHERE signal.signal_id = ?
    """;

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, signalId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}