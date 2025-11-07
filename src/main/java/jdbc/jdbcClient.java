package jdbc;

import jdbcInterfaces.ClientManager;
import pojos.Client;
import pojos.Sex;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;


public class jdbcClient implements ClientManager {

    @Override
    public int addClient(Client client) {
        String sql = """
            INSERT INTO Client (name, surname, dob, mail, sex, doctor_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        int generatedId = -1;
        jdbcConnectionManager cm = new jdbcConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getSurname());
            ps.setString(3, client.getDob() != null ? client.getDob().toString() : null);
            ps.setString(4, client.getMail());
            ps.setString(5, client.getSex() != null ? client.getSex().name() : null);
            //ps.setInt(6, client.getDoctorId());

            // ⚠️ Aquí está el cambio importante:
            if (client.getDoctorId() > 0) {
                ps.setInt(6, client.getDoctorId());
            } else {
                ps.setNull(6, Types.INTEGER); // ← Esto evita el error de clave foránea
            }

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
                client.setClientId(generatedId);
            }

            System.out.println("Cliente insertado correctamente con ID: " + generatedId);

        } catch (SQLException e) {
            System.err.println("Error al insertar cliente:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }

        return generatedId;
    }

    @Override
    public Client getClientById(int clientId) {
        String sql = "SELECT * FROM Client WHERE client_id = ?";
        Client c = null;

        jdbcConnectionManager cm = new jdbcConnectionManager();

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
                if (dob != null) {
                    c.setDob(LocalDate.parse(dob));
                }

                c.setMail(rs.getString("mail"));

                String sex = rs.getString("sex");
                if (sex != null) {
                    c.setSex(Sex.valueOf(sex));
                }

                c.setDoctorId(rs.getInt("doctor_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por ID:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }

        return c;
    }

    @Override
    public List<Client> getClients() {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM Client";

        jdbcConnectionManager cm = new jdbcConnectionManager();

        try (Connection conn = cm.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Client c = new Client();
                c.setClientId(rs.getInt("client_id"));
                c.setName(rs.getString("name"));
                c.setSurname(rs.getString("surname"));

                String dob = rs.getString("dob");
                if (dob != null) {
                    c.setDob(LocalDate.parse(dob));
                }

                c.setMail(rs.getString("mail"));

                String sex = rs.getString("sex");
                if (sex != null) {
                    c.setSex(Sex.valueOf(sex));
                }

                c.setDoctorId(rs.getInt("doctor_id"));

                list.add(c);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener lista de clientes:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }

        return list;
    }

    @Override
    public void updateClient(Client client) {
        String sql = """
            UPDATE Client
            SET name=?, surname=?, dob=?, mail=?, sex=?, doctor_id=?
            WHERE client_id=?
        """;

        jdbcConnectionManager cm = new jdbcConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getSurname());
            ps.setString(3, client.getDob() != null ? client.getDob().toString() : null);
            ps.setString(4, client.getMail());
            ps.setString(5, client.getSex() != null ? client.getSex().name() : null);
            //ps.setInt(6, client.getDoctorId());
            if (client.getDoctorId() > 0) {
                ps.setInt(6, client.getDoctorId());
            } else {
                ps.setNull(6, Types.INTEGER);  // 💡 aquí el cambio
            }
            ps.setInt(7, client.getClientId());

            ps.executeUpdate();
            System.out.println("Cliente actualizado correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }
    }

    @Override
    public void deleteClient(int clientId) {
        String sql = "DELETE FROM Client WHERE client_id = ?";

        jdbcConnectionManager cm = new jdbcConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            ps.executeUpdate();
            System.out.println("Cliente eliminado correctamente.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }
    }
}

