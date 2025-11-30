package jdbcInterfaces;

import pojos.User;

import java.sql.Connection;

/**
 * Manages persistence and authentication operations for the {@code User} entity
 * in the database.
 */
public interface UserManager {

    /**
     * Inserts a new user record into the database, hashing the password before storage.
     *
     * @param user The {@code User} object to add, containing the username and plain password.
     * @return The auto-generated ID of the newly inserted user, or -1 if the operation fails (e.g., username already exists).
     */
    int addUser(User user);

    /**
     * Validates a user's login credentials against the database.
     *
     * @param username The username provided by the user.
     * @param password The plain text password provided by the user.
     * @return A {@code User} object (containing ID and username) if the credentials are valid, or null otherwise.
     */
    User validateLogin(String username, String password);

    /**
     * Inserts a default user specifically for the doctor profile into the 'user' table if it does not already exist.
     *
     * @param conn The active database connection.
     */
    void insertDefaultDoctorUser(Connection conn);

    /**
     * Retrieves the user ID for a given username, primarily used for linking user and profile tables (e.g., Doctor/Client).
     *
     * @param username The username to look up.
     * @return The ID of the user if the username exists, or -1 otherwise.
     */
    int getUserIdByUsernameOnlyIfExists(String username);
}