package jdbc;

import jdbcInterfaces.UserManager;
import pojos.User;

import java.sql.*;

public class JDBCUser implements UserManager {


    @Override
    public int addUser(User user) {
        String sql = "INSERT INTO user (username, password) VALUES (?, ?)";
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setBytes(2, user.getPassword());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.id = rs.getInt(1);
                }
            }

            System.out.println("Usuario insertado con ID: " + user.getId());

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario:");
            e.printStackTrace();
        }

        return user.id;
    }


    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";

        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getBytes("password"));
                return u;
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por nombre de usuario:");
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public void deleteUser(int id) {
        String sql = "DELETE FROM user WHERE id = ?";
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Usuario eliminado correctamente por ID.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario por ID:");
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUserByName(String username) {
        String sql = "DELETE FROM user WHERE username = ?";
        JDBCConnectionManager cm = new JDBCConnectionManager();

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.executeUpdate();
            System.out.println("Usuario eliminado correctamente por nombre.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario por nombre:");
            e.printStackTrace();
        }
    }

    @Override
    public User validateLogin(String username, String password) {
        JDBCConnectionManager cm = new JDBCConnectionManager();

        String sql = """
            SELECT user_id, username, password
            FROM user
            WHERE username = ? AND password = ?
        """;

        try (Connection conn = cm.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("user_id");
                String uname = rs.getString("username");
                String pw = rs.getString("password");

                User u = new User(id, pw.getBytes());
                u.setUsername(uname);
                return u;
            }

        } catch (SQLException e) {
            System.out.println("Error validating login: " + e.getMessage());
        }

        return null;
    }
}

