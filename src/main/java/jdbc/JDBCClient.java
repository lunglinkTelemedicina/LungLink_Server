package jdbc;

import jdbcInterfaces.ClientManager;
import pojos.Client;
import pojos.Sex;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JDBCClient implements ClientManager {


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

    @Override
    public Client getClientById(int clientId) {

        String sql = "SELECT * FROM client WHERE client_id = ?";
        Client c = null;

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                c = new Client();
                c.setClientId(rs.getInt("client_id"));
                c.setName(rs.getString("name"));
                c.setSurname(rs.getString("surname"));

                String dob = rs.getString("dob");
                if (dob != null) c.setDob(LocalDate.parse(dob));

                c.setMail(rs.getString("mail"));

                String sex = rs.getString("sex");
                if (sex != null) c.setSex(Sex.valueOf(sex));

                c.setWeight(rs.getDouble("weight"));
                c.setHeight(rs.getDouble("height"));

                c.setDoctorId(rs.getInt("doctor_id"));
                c.setUserId(rs.getInt("user_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error when obtaining a client with its id:");
            e.printStackTrace();
        }

        return c;
    }

    @Override
    public List<Client> getClients() {

        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM client";


        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Client c = new Client();
                c.setClientId(rs.getInt("client_id"));
                c.setName(rs.getString("name"));
                c.setSurname(rs.getString("surname"));

                String dob = rs.getString("dob");
                if (dob != null) c.setDob(LocalDate.parse(dob));

                c.setMail(rs.getString("mail"));

                String sex = rs.getString("sex");
                if (sex != null) c.setSex(Sex.valueOf(sex));

                c.setWeight(rs.getDouble("weight"));
                c.setHeight(rs.getDouble("height"));

                c.setDoctorId(rs.getInt("doctor_id"));
                c.setUserId(rs.getInt("user_id"));

                list.add(c);
            }

        } catch (SQLException e) {
            System.err.println("Error when obtaining the list of clients: ");
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void updateClient(Client client) {

        String sql = """
            UPDATE client
            SET name = ?, surname = ?, dob = ?, mail = ?, sex = ?, weight = ?, height = ?, doctor_id = ?, user_id = ?
            WHERE client_id = ?
        """;


        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getSurname());
            ps.setString(3, client.getDob() != null ? client.getDob().toString() : null);
            ps.setString(4, client.getMail());
            ps.setString(5, client.getSex() != null ? client.getSex().name() : null);
            ps.setDouble(6, client.getWeight());
            ps.setDouble(7, client.getHeight());
            ps.setInt(8, client.getDoctorId());
            ps.setInt(9, client.getUserId());
            ps.setInt(10, client.getClientId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Client with ID  " + client.getClientId() + " correctly updated");
            } else {
                System.out.println("Client not found");
            }

        } catch (SQLException e) {
            System.err.println("Error when updating client");
            e.printStackTrace();
        }
    }

    @Override
    public void deleteClient(int clientId) {
        String sql = "DELETE FROM client WHERE client_id = ?";
        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Client with ID " + clientId + " correctly deleted ");
            } else {
                System.out.println("Client not found");
            }

        } catch (SQLException e) {
            System.err.println("Error when eliminating the client");
            e.printStackTrace();
        }
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

        //JDBCConnectionManager cm = new  JDBCConnectionManager();
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

                // DOB → LocalDate
                String dob = rs.getString("dob");
                LocalDate date = LocalDate.parse(dob);
                c.setDob(date);

                c.setSex(Sex.valueOf(rs.getString("sex")));

                // Optional fields
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

            ps.setInt(1, doctorId);
            ps.setInt(2, clientId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Doctor " + doctorId + " assigned to client " + clientId);
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

        JDBCConnectionManager cm = new JDBCConnectionManager();

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
