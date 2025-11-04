package jdbcInterfaces;

import pojos.User;

public interface UserManager {

    public void addUser(User user);
    public User getUserByUsername(String username);
    public void deleteUser(String username);

}
