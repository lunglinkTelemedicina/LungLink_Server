package jdbcInterfaces;

import pojos.User;

public interface UserManager {

    public int addUser(User user);
    public User getUserByUsername(String username);
    public void deleteUser(int userId);
    public void deleteUserByName(String username);
}
