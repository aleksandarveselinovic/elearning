package elearning.components;

import elearning.entities.User;
import elearning.pages.Index;
import elearning.services.UserManager;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import javax.inject.Inject;

@Import(
    stylesheet = {
        "classpath:/bootstrap/css/bootstrap.min.css",
        "classpath:/bootstrap/css/bootstrap-responsive.min.css",
        "context:style.css"
    },
    library = "classpath:/bootstrap/js/bootstrap.min.js"
)
public class Layout {

    @Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
    @Property
    private String title;

    @Inject
    private UserManager userManager;

    public User getUser() {
        return userManager.getCurrentUser();
    }

    @OnEvent("logout")
    Object logout() {
        userManager.logout();
        return Index.class;
    }

}
