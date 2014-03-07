package elearning.pages.admin;

import elearning.entities.Course;
import elearning.services.TestService;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;

import javax.inject.Inject;

public class CourseEdit {

    @Property
    @PageActivationContext
    private Course course;

    @Inject
    private TestService testService;

    @OnEvent(EventConstants.PREPARE)
    void prepare() {
        if (course == null) {
            course = new Course();
        }
    }

    @OnEvent(EventConstants.SUCCESS)
    Object save() {
        testService.persist(course);
        return Courses.class;
    }

    public boolean getCanDelete(){
        return course.getId() != null;
    }

    @OnEvent("delete")
    Object delete(Course course) {
        testService.delete(course);
        return Courses.class;
    }

}
