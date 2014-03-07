package elearning.pages.user;

import elearning.entities.Course;
import elearning.entities.Role;
import elearning.entities.Test;
import elearning.services.LabelProvider;
import elearning.services.TapestryHelper;
import elearning.services.TestService;
import elearning.services.UserManager;
import elearning.utils.CriteriaConfigurer;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import java.text.DateFormat;

public class TestResults {

    @Property
    private Test test;

    @Inject
    private TapestryHelper tapestryHelper;

    @Inject
    private UserManager userManager;

    @Inject
    private TestService testService;

    public GridDataSource getDataSource() {
        return tapestryHelper.getDataSource(Test.class, new CriteriaConfigurer() {
            @Override
            public void configure(Criteria criteria) {
                if (isStudent()) {
                    criteria.add(Restrictions.eq("user", userManager.getCurrentUser()));
                }
            }
        });
    }

    private boolean isStudent() {
        return userManager.hasRole(Role.Student);
    }

    public DateFormat getTimeFormat() {
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    }

    public boolean isPassed() {
        return test.getCorrectAnswers() >= test.getTemplate().getQuestionsToPass();
    }

    public String getTestStatusCssClass() {
        return isPassed() ? "passed" : "failed";
    }

    public SelectModel getCourseModel() {
        return tapestryHelper.selectModel(Course.class, testService.getAllCourses(), new LabelProvider<Course>() {
            @Override
            public String getLabel(Course course) {
                return course.getTitle();
            }
        });
    }

}
