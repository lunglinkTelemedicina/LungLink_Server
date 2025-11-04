package pojos;

public class User {
    private int id;
    public String username;
    public byte[] password;

    public User(int id, String username, byte[] password) {
        this.id = id;
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

    public byte[] getPassword() {
        return password;
    }
    public void setPassword(byte[] password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" + "username=" + username + ", password=" + password + ", userId=" + id + '}';
    }
}
