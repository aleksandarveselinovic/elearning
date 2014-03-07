package elearning.pages.student;

import elearning.entities.TestTemplate;
import elearning.services.TapestryHelper;
import elearning.services.TestService;
import elearning.services.TestSessionManager;
import elearning.services.UserManager;
import elearning.utils.CriteriaConfigurer;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import java.util.Date;

public class Tests {

    @Property
    private TestTemplate template;

    @Inject
    private UserManager userManager;

    @Inject
    private TestService testService;

    @Inject
    private TapestryHelper tapestryHelper;

    @Inject
    private TestSessionManager testSessionManager;

    public GridDataSource getDataSource() {
        return tapestryHelper.getDataSource(TestTemplate.class, new CriteriaConfigurer() {
            @Override
            public void configure(Criteria criteria) {
                Date now = new Date();
                criteria.add(Restrictions.le("startDate", now));
                criteria.add(Restrictions.ge("endDate", now));
            }
        });
    }

    @OnEvent("createTest")
    Object createTest(TestTemplate template) {
        this.template = template;
        if (getCanAttempt()) {
            testSessionManager.create(template);
            return ShowQuestion.class;
        }

        return null;
    }

    public boolean getCanAttempt() {
        return testService.getTests(template, userManager.getCurrentUser()).size() < template.getAttemptsAllowed();
    }
}
