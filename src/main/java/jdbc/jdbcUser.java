package jdbc;

import jdbcInterfaces.UserManager;
import pojos.User;

import db.DBConnection;
import java.sql.*;


public class jdbcUser implements UserManager{
    @Override
    public int addUser(User user) {

        String sql = "INSERT INTO user(username, password) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.username);
            ps.setBytes(2, user.password);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;

    }

    @Override
    public User getUserByUsername(String username) {

        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.id = rs.getInt("id");
                u.username = rs.getString("username");
                u.password = rs.getBytes("password");
                return u;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteUser(int id) {

        String sql = "DELETE FROM user WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUserByName(String username) {

        String sql = "DELETE FROM user WHERE username=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
