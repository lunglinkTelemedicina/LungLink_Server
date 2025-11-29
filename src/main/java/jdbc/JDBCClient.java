package jdbc;

import jdbcInterfaces.*;
import pojos.*;

import java.sql.*;
import java.time.LocalDate;

public class JDBCClient implements ClientManager {

    private static JDBCClient instance;

    public static synchronized JDBCClient getInstance() {
        if (instance == null) {
            instance = new JDBCClient();
        }
        return instance;
    }

    @Override
    public int addClient(Client client) {
        String sql = """
            INSERT INTO client (name, surname, dob, mail, sex, weight, height, user_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        int generatedId = -1;

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getSurname());
            ps.setString(3, client.getDob() != null ? client.getDob().toString() : null);
            ps.setString(4, client.getMail());
            ps.setString(5, client.getSex() != null ? client.getSex().name() : null);
            ps.setDouble(6, client.getWeight());
            ps.setDouble(7, client.getHeight());
            ps.setInt(8, client.getUserId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    client.setClientId(generatedId);
                }
            }

            System.out.println("Client correctly inserted with ID: " + generatedId);

        } catch (SQLException e) {
            System.err.println("Error when inserting a client:");
            e.printStackTrace();
        }

        return generatedId;
    }

    public void updateHeightWeight(int clientId, double weight, double height) {

        String sql = """
        UPDATE client
        SET weight = ?, height = ?
        WHERE client_id = ?
    """;


        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, weight);
            ps.setDouble(2, height);
            ps.setInt(3, clientId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Weight and height correctly updated for client with id \n" + clientId);
            } else {
                System.out.println("Client not found");
            }

        } catch (SQLException e) {
            System.err.println("Error while updating height and weight");
            e.printStackTrace();
        }
    }

    public Client getClientByUserId(int userId) {

        String sql = """
            SELECT client_id, name, surname, dob, sex, mail, height, weight
            FROM client
            WHERE user_id = ?
        """;

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Client c = new Client();
                c.setClientId(rs.getInt("client_id"));
                c.setUserId(userId);
                c.setName(rs.getString("name"));
                c.setSurname(rs.getString("surname"));
                c.setMail(rs.getString("mail"));

                String dob = rs.getString("dob");
                LocalDate date = LocalDate.parse(dob);
                c.setDob(date);

                c.setSex(Sex.valueOf(rs.getString("sex")));

                c.setHeight(rs.getDouble("height"));
                c.setWeight(rs.getDouble("weight"));

                return c;
            }

        } catch (SQLException e) {
            System.out.println("Error loading client: " + e.getMessage());
        }

        return null;
    }

    public void updateDoctorForClient(int clientId, int doctorId) {

        String sql = "UPDATE client SET doctor_id = ? WHERE client_id = ?";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (doctorId > 0) {
                ps.setInt(1, doctorId);
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, clientId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                if (doctorId > 0)
                    System.out.println("Doctor " + doctorId + " assigned to client " + clientId);
                else
                    System.out.println("Doctor NULL assigned to client " + clientId);
            } else {
                System.out.println("No client found with ID " + clientId);
            }

        } catch (SQLException e) {
            System.err.println("Error assigning doctor to client");
            e.printStackTrace();
        }
    }

    public int countClientsByDoctor(int doctorId) {

        String sql = "SELECT COUNT(*) FROM client WHERE doctor_id = ?";

        JDBCConnectionManager cm = JDBCConnectionManager.getInstance();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


}
