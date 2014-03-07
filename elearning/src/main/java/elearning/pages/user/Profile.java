package elearning.pages.user;

import elearning.entities.User;
import elearning.services.UserManager;
import javax.inject.Inject;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;

public class Profile {

    @Inject
    private UserManager userManager;

    public User getUser() {
        return userManager.getCurrentUser();
    }

    @OnEvent(EventConstants.SUCCESS)
    void save() {
        userManager.persist(getUser());
    }

}
