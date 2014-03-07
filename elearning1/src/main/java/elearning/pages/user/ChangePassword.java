package elearning.pages.user;

import elearning.services.UserManager;
import javax.inject.Inject;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;

public class ChangePassword {

    @Property
    private String password;

    @Property
    private String confirmPassword;

    @Environmental
    private ValidationTracker tracker;

    @Inject
    private UserManager userManager;

    @Inject
    private AlertManager alertManager;

    @Inject
    private Messages messages;

    @OnEvent(value = EventConstants.VALIDATE, component = "changePasswordForm")
    void validate() {
        if (tracker.getHasErrors()) {
            return;
        }

        if (!password.equals(confirmPassword)) {
            tracker.recordError("passwords-do-not-match");
        }
    }

    @OnEvent(EventConstants.SUCCESS)
    void changePassword() {
        userManager.changePassword(userManager.getCurrentUser(), password);
        alertManager.info(messages.get("password-changed-successfully"));
    }

}
