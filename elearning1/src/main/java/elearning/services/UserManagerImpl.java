package elearning.services;

import elearning.entities.Role;
import elearning.entities.User;
import java.util.List;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.ioc.services.PerThreadValue;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class UserManagerImpl implements UserManager {

    private final Session session;

    private PerThreadValue<User> user;

    public UserManagerImpl(PerthreadManager perthreadManager, Session session) {
        this.session = session;
        user = perthreadManager.createValue();
    }

    @Override
    public void changePassword(User user, String password) {
        user.setPassword(password);
        persist(user);

        if (!isLoggedIn()) {
            authenticate(user.getUsername(), password);
        }
    }

    @Override
    public void persist(User user) {
        session.saveOrUpdate(user);
    }

    @Override
    public void authenticate(String username, String password) {
        getSubject().login(new UsernamePasswordToken(username, password));
    }

    @Override
    public void logout() {
        getSubject().logout();
    }

    private Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    @Override
    public boolean isLoggedIn() {
        return getSubject().isRemembered() || getSubject().isAuthenticated();
    }

    @Override
    public User getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }

        User value = user.get();
        if (value == null) {
            value = findByUsername(getSubject().getPrincipal().toString());
            user.set(value);
        }

        return value;
    }

    @Override
    public User findByUsername(String username) {
        return (User) session.createCriteria(User.class)
            .add(Restrictions.eq("username", username))
            .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> getAllUsers() {
        return session.createCriteria(User.class).list();
    }

    @Override
    public void delete(User user) {
        session.delete(user);
    }

    @Override
    public boolean hasRole(Role role) {
        return hasRole(role.name());
    }

    @Override
    public boolean hasRole(String role) {
        return getSubject().hasRole(role);
    }

}
