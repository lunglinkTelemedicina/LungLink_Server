package jdbcInterfaces;

import pojos.User;

public interface UserManager {

    int addUser(User user);
    User getUserByUsername(String username);
    void deleteUser(int userId);
    void deleteUserByName(String username);
    User validateLogin(String username, String password);
}
