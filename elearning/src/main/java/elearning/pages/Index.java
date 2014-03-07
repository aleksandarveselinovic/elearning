package elearning.pages;

import elearning.pages.user.Profile;
import elearning.services.UserManager;
import javax.inject.Inject;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;

public class Index {

    @Property
    private String username;

    @Property
    private String password;

    @Inject
    private UserManager userManager;

    @Environmental
    private ValidationTracker tracker;

    @Inject
    private Messages messages;

    @OnEvent(component = "loginForm", value = EventConstants.VALIDATE)
    void login() {
        try {
            userManager.authenticate(username, password);
        } catch (Exception ex) {
            tracker.recordError(messages.get("invalid-username-or-password"));
        }
    }

    @OnEvent(EventConstants.SUCCESS)
    Object toHomePage() {
        return Profile.class;
    }

}
