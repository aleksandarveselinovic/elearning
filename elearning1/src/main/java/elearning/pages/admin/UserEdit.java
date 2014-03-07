package elearning.pages.admin;

import elearning.entities.User;
import elearning.services.UserManager;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;

import javax.inject.Inject;

public class UserEdit {

    @Property
    @PageActivationContext
    private User user;

    @Inject
    private UserManager userManager;

    @OnEvent(EventConstants.PREPARE)
    void prepare(){
        if(user == null){
            user = new User();
        }
    }

    @OnEvent(EventConstants.SUCCESS)
    Object save() {
        userManager.persist(user);
        return Users.class;
    }

    public boolean getCanDelete(){
        return user.getId() != null && !user.getId().equals(userManager.getCurrentUser().getId());
    }

    @OnEvent("delete")
    Object delete(User user) {
        userManager.delete(user);
        return Users.class;
    }

}
