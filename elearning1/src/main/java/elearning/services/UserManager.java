package elearning.services;

import elearning.entities.Role;
import elearning.entities.User;
import java.util.List;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;

public interface UserManager {

    @CommitAfter
    void changePassword(User user, String password);

    @CommitAfter
    void authenticate(String username, String password);

    void logout();

    boolean isLoggedIn();

    User getCurrentUser();

    boolean hasRole(String role);

    @CommitAfter
    void persist(User user);

    User findByUsername(String username);

    List<User> getAllUsers();

    @CommitAfter
    void delete(User user);

    boolean hasRole(Role role);

}
