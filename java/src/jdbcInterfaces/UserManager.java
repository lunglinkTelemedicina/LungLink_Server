package src.jdbcInterfaces;

import src.pojos.User;

public interface UserManager {

    public void insert(User user);
    public User findByUsername(String username);
    public void delete(String username);

}
