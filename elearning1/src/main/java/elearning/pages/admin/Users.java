package elearning.pages.admin;

import elearning.entities.User;
import elearning.services.TapestryHelper;
import elearning.services.UserManager;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;

import javax.inject.Inject;

public class Users {


    @Property
    private User user;

    @Inject
    private TapestryHelper tapestryHelper;

    @Inject
    private UserManager userManager;

    public GridDataSource getDataSource(){
        return tapestryHelper.getDataSource(User.class);
    }

    @OnEvent("delete")
    void delete(User user){
        userManager.delete(user);
    }

}
