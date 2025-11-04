package jdbc;

import db.DBConnection;
import jdbcInterfaces.ClientManager;
import pojos.Client;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;


public class jdbcClient implements ClientManager {


    @Override
    public void addClient(Client client) {

        String sql = "INSERT INTO clients (username, fullname, age, sex) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getUsername());
            ps.setString(2, c.getFullName());
            ps.setInt(3, c.getAge());
            ps.setString(4, c.getSex().toString());
            ps.executeUpdate();

        } catch (SQLException e) { e.printStackTrace(); }

    }

    @Override
    public Client getClientById(int id) {
        return null;
    }

    @Override
    public List<Client> getClients() {
        return List.of();
    }

    @Override
    public void updateClient(Client client) {

    }

    @Override
    public void deleteClient(int id) {

    }
}
