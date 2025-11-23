package jdbc;

import jdbcInterfaces.UserManager;
import pojos.User;

import java.sql.*;

public class JDBCUser implements UserManager {


    @Override
    public int addUser(User user) {
        String sql = "INSERT INTO user (username, password) VALUES (?, ?)";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String hashedPassword = utils.SecurityUtils.hashPassword(user.getPassword());

            ps.setString(1, user.getUsername());
            ps.setString(2, hashedPassword);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.id = rs.getInt(1);
                }
            }

            System.out.println("User correctly added with ID: \n" + user.getId());

        } catch (SQLException e) {
            System.err.println("Error when adding a user");
            e.printStackTrace();
        }

        return user.id;
    }


    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";


        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                return u;
            }

        } catch (SQLException e) {
            System.err.println("Error when obtaining the user by its username");
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public void deleteUser(int id) {
        String sql = "DELETE FROM user WHERE id = ?";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("User " + id + "correctly deleted");

        } catch (SQLException e) {
            System.err.println("Error when deleting user by id: ");
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUserByName(String username) {
        String sql = "DELETE FROM user WHERE username = ?";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.executeUpdate();
            System.out.println("User " + username + " correctly deleted ");

        } catch (SQLException e) {
            System.err.println("Error when deleting user by name: ");
            e.printStackTrace();
        }
    }

    @Override
    public User validateLogin(String username, String passwordPlain) {


        String passwordHash = utils.SecurityUtils.hashPassword(passwordPlain);

        String sql = """
            SELECT id, username, password
            FROM user
            WHERE username = ? AND password = ?
        """;

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String uname = rs.getString("username");
                String pwHash = rs.getString("password");

                User u = new User(id, pwHash);
                u.setUsername(uname);
                return u;
            }

        } catch (SQLException e) {
            System.out.println("Error validating login: " + e.getMessage());
        }

        return null;
    }
}

