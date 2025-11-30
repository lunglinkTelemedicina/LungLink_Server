package jdbc;

import jdbcInterfaces.UserManager;
import pojos.User;
import utils.SecurityUtils;

import java.sql.*;
/**
 * Implementation of the {@code UserManager} interface using JDBC
 * for managing user persistence in the database.
 * This class handles user registration, login validation (using hashed passwords),
 * and management of the default doctor user entry.
 *
 * This class follows the Singleton pattern.
 */
public class JDBCUser implements UserManager {

    private static JDBCUser instance;
    /**
     * Retrieves the single instance of the JDBCUser class (Singleton pattern).
     *
     * @return The JDBCUser instance.
     */
    public static synchronized JDBCUser getInstance() {
        if (instance == null) {
            instance = new JDBCUser();
        }
        return instance;
    }
    /**
     * Inserts a new user into the 'user' table after hashing the password.
     *
     * @param user The User object to insert, containing username and plain password.
     * @return The automatically generated user ID, or -1 if the insertion fails (e.g., username already exists).
     */
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
    /**
     * Validates a user's login credentials by hashing the provided plain password
     * and checking against the stored hash in the database.
     *
     * @param username The username provided by the user.
     * @param passwordPlain The plain text password provided by the user.
     * @return A partially populated User object (containing ID and username) if credentials are valid, or null otherwise.
     */
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
    /**
     * Inserts the default doctor user ("AlfredoJimenez" with password "doctor123") into the 'user' table
     * if the user does not already exist.
     *
     * @param conn The active database connection.
     * @implNote This method is designed to be called during database initialization.
     */
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

    /**
     * Retrieves the user ID for a given username, primarily used to link the default doctor profile.
     *
     * @param username The username to look up.
     * @return The user ID if the username exists, or -1 otherwise.
     */
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