package jdbc;

import jdbcInterfaces.UserManager;
import pojos.User;

import java.sql.*;

public class jdbcUser implements UserManager {

    @Override
    public int addUser(User user) {
        String sql = "INSERT INTO User (username, password) VALUES (?, ?)";
        int generatedId = -1;

        jdbcConnectionManager cm = new jdbcConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.username);
            ps.setBytes(2, user.password);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    user.id = generatedId;
                }
            }

            System.out.println("Usuario insertado correctamente con ID: " + generatedId);

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }

        return generatedId;
    }

    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM User WHERE username = ?";
        User u = null;

        jdbcConnectionManager cm = new jdbcConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                u = new User();
                u.id = rs.getInt("id");
                u.username = rs.getString("username");
                u.password = rs.getBytes("password");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por nombre de usuario:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }

        return u;
    }

    @Override
    public void deleteUser(int id) {
        String sql = "DELETE FROM User WHERE id = ?";
        jdbcConnectionManager cm = new jdbcConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Usuario eliminado correctamente por ID.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario por ID:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }
    }

    @Override
    public void deleteUserByName(String username) {
        String sql = "DELETE FROM User WHERE username = ?";
        jdbcConnectionManager cm = new jdbcConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.executeUpdate();
            System.out.println("Usuario eliminado correctamente por nombre.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario por nombre:");
            e.printStackTrace();
        } finally {
            cm.disconnect();
        }
    }
}
