package jdbcInterfaces;

import pojos.User;

import java.sql.Connection;

public interface UserManager {

    int addUser(User user);
    User validateLogin(String username, String password);
    void insertDefaultDoctorUser(Connection conn);
    int getUserIdByUsernameOnlyIfExists(String username);
}
