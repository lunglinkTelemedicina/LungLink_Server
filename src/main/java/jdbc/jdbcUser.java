package jdbc;

import jdbcInterfaces.UserManager;
import pojos.User;

import java.util.List;


public class jdbcUser implements UserManager{
    @Override
    public void addUser(User user) {

    }

    @Override
    public User getUserByUsername(String username) {
        return null;
    }

    @Override
    public void deleteUser(String username) {

    }
}
