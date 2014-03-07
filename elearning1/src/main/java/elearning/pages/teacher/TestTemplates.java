package elearning.pages.teacher;

import elearning.entities.TestTemplate;
import elearning.services.TapestryHelper;
import elearning.services.TestService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;

import javax.inject.Inject;

public class TestTemplates {

    @Property
    private TestTemplate template;

    @Inject
    private TestService testService;

    @Inject
    private TapestryHelper tapestryHelper;

    public GridDataSource getAllTemplates() {
        return tapestryHelper.getDataSource(TestTemplate.class);
    }

    void delete(TestTemplate template) {
        testService.delete(template);
    }

}
