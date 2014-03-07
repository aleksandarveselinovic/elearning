package elearning.pages.teacher;

import elearning.entities.Course;
import elearning.services.TapestryHelper;
import elearning.services.TestService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;

import javax.inject.Inject;

public class Courses {

    @Property
    private Course course;

    @Inject
    private TapestryHelper tapestryHelper;

    @Inject
    private TestService testService;

    public GridDataSource getAllCourses() {
        return tapestryHelper.getDataSource(Course.class);
    }

    public int getQuestionCount(){
        return testService.getQuestionCount(course);
    }

}
