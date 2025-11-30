package pojos;

/**
 * Represents a user account in the system, storing login credentials
 * and the internal user ID.
 */
public class User {
    public int id;
    public String username;
    public String password;

    /**
     * Creates a complete User.
     */
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * Creates a User with ID and password only.
     */
    public User(int id, String password) {
        this.id = id;
        this.password = password;
    }

    /**
     * Creates a User without ID (used before inserting into database).
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int  getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" + "username=" + username + ", password=" + password + ", userId=" + id + '}';
    }
}
