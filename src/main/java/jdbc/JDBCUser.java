package jdbc;

import jdbcInterfaces.UserManager;
import pojos.User;
import utils.SecurityUtils;

import java.sql.*;

public class JDBCUser implements UserManager {

    private static JDBCUser instance;

    public static synchronized JDBCUser getInstance() {
        if (instance == null) {
            instance = new JDBCUser();
        }
        return instance;
    }

    @Override
    public int addUser(User user) {
        String sql = "INSERT INTO user (username, password) VALUES (?, ?)";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String hashedPassword = SecurityUtils.hashPassword(user.getPassword());

            ps.setString(1, user.getUsername());
            ps.setString(2, hashedPassword);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.id = rs.getInt(1);
                }
            }

            System.out.println("User correctly added with ID: " + user.getId() + "\n");
            return user.id;

        } catch (SQLException e) {

            if (e.getMessage().contains("UNIQUE constraint failed: user.username")) {
                System.out.println("Username already exists: " + user.getUsername());
                return -1;
            }
            System.err.println("Error when adding a user");
            e.printStackTrace();
            return -1;
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

    public void insertDefaultDoctorUser(Connection conn) {

        String sqlCheck = "SELECT COUNT(*) FROM user WHERE username = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlCheck)) {

            ps.setString(1, "AlfredoJimenez");
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Default doctor USER already exists.");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String sqlInsert = "INSERT INTO user (username, password) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {

            String hashedPassword = utils.SecurityUtils.hashPassword("doctor123");

            ps.setString(1, "AlfredoJimenez");
            ps.setString(2, hashedPassword);
            ps.executeUpdate();

            System.out.println("Default doctor USER created.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getUserIdByUsernameOnlyIfExists(String username) {

        String sql = "SELECT id FROM user WHERE username = ?";

        try (Connection conn = JDBCConnectionManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }


}

