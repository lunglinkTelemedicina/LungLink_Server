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
            INSERT INTO client (name, surname, dob, mail, sex, doctor_id, user_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        int generatedId = -1;
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getSurname());
            ps.setString(3, client.getDob() != null ? client.getDob().toString() : null);
            ps.setString(4, client.getMail());
            ps.setString(5, client.getSex() != null ? client.getSex().name() : null);
            ps.setInt(6, client.getDoctorId());
            ps.setInt(7, client.getUserId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    client.setClientId(generatedId);
                }
            }

            System.out.println("Cliente insertado correctamente con ID: " + generatedId);

        } catch (SQLException e) {
            System.err.println("Error al insertar cliente:");
            e.printStackTrace();
        }

        return generatedId;
    }

    @Override
    public Client getClientById(int clientId) {
        String sql = "SELECT * FROM client WHERE client_id = ?";
        Client c = null;

        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
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

                c.setDoctorId(rs.getInt("doctor_id"));
                c.setUserId(rs.getInt("user_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por ID:");
            e.printStackTrace();
        }

        return c;
    }

    @Override
    public List<Client> getClients() {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM client";

        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
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

                c.setDoctorId(rs.getInt("doctor_id"));
                c.setUserId(rs.getInt("user_id"));

                list.add(c);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener lista de clientes:");
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void updateClient(Client client) {
        String sql = """
            UPDATE client
            SET name = ?, surname = ?, dob = ?, mail = ?, sex = ?, doctor_id = ?, user_id = ?
            WHERE client_id = ?
        """;

        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getSurname());
            ps.setString(3, client.getDob() != null ? client.getDob().toString() : null);
            ps.setString(4, client.getMail());
            ps.setString(5, client.getSex() != null ? client.getSex().name() : null);
            ps.setInt(6, client.getDoctorId());
            ps.setInt(7, client.getUserId());
            ps.setInt(8, client.getClientId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Cliente actualizado correctamente (ID " + client.getClientId() + ")");
            } else {
                System.out.println("No se encontró cliente con ese ID.");
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente:");
            e.printStackTrace();
        }
    }

    @Override
    public void deleteClient(int clientId) {
        String sql = "DELETE FROM client WHERE client_id = ?";
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Cliente eliminado correctamente (ID " + clientId + ")");
            } else {
                System.out.println("No se encontró cliente con ese ID.");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente:");
            e.printStackTrace();
        }
    }
}
