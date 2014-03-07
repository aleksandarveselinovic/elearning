package elearning.pages.admin;

import elearning.entities.Course;
import elearning.services.TapestryHelper;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;

import javax.inject.Inject;

public class Courses {

    @Property
    private Course course;

    @Inject
    private TapestryHelper tapestryHelper;

    public GridDataSource getAllCourses() {
        return tapestryHelper.getDataSource(Course.class);
    }

}
