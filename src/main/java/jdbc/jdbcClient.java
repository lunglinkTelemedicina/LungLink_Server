package jdbc;

import jdbcInterfaces.ClientManager;
import pojos.Client;
import pojos.Sex;

import db.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;


public class jdbcClient implements ClientManager {


    @Override
    public int addClient(Client client) {

        String sql = "INSERT INTO client(name, surname, dob, mail, sex, doctor_id, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getSurname());
            ps.setString(3, client.getDob().toString());
            ps.setString(4, client.getMail());
            ps.setString(5, client.getSex().name());
            ps.setInt(6, client.getDoctorId());
           // ps.setInt(7, client.getUserId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Client getClientById(int clientId) {

        String sql = "SELECT * FROM client WHERE client_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Client c = new Client();
                c.setClientId(rs.getInt("client_id"));
                c.setName(rs.getString("name"));
                c.setSurname(rs.getString("surname"));
                c.setDob(LocalDate.parse(rs.getString("dob")));
                c.setMail(rs.getString("mail"));
                c.setSex(Sex.valueOf(rs.getString("sex")));
                c.setDoctorId(rs.getInt("doctor_id"));
                //c.setUserId(rs.getInt("user_id"));
                return c;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Client> getClients() {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM client";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Client c = new Client();
                c.setClientId(rs.getInt("client_id"));
                c.setName(rs.getString("name"));
                c.setSurname(rs.getString("surname"));
                c.setDob(LocalDate.parse(rs.getString("dob")));
                c.setMail(rs.getString("mail"));
                c.setSex(Sex.valueOf(rs.getString("sex")));
                c.setDoctorId(rs.getInt("doctor_id"));
                //c.setUserId(rs.getInt("user_id"));
                list.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateClient(Client client) {

        String sql = "UPDATE client SET name=?, surname=?, dob=?, mail=?, sex=?, doctor_id=?, user_id=? WHERE client_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getSurname());
            ps.setString(3, client.getDob().toString());
            ps.setString(4, client.getMail());
            ps.setString(5, client.getSex().name());
            ps.setInt(6, client.getDoctorId());
           // ps.setInt(7, client.getUserId());
            ps.setInt(8, client.getClientId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteClient(int clientId) {

        String sql = "DELETE FROM client WHERE client_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    }

