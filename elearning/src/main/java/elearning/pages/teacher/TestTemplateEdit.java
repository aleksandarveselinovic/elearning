package elearning.pages.teacher;

import elearning.entities.Course;
import elearning.entities.TestTemplate;
import elearning.services.TapestryHelper;
import elearning.services.TestService;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

public class TestTemplateEdit {

    @PageActivationContext
    @Property
    private TestTemplate template;

    @Inject
    private TestService testService;

    @Inject
    private TapestryHelper tapestryHelper;

    @Inject
    private Messages messages;

    @Environmental
    private ValidationTracker validationTracker;

    @OnEvent(EventConstants.PREPARE)
    void prepare(){
        if(template == null){
            template = new TestTemplate();
        }
    }

    @OnEvent(component = "templateForm", value = EventConstants.VALIDATE)
    void validate() throws ValidationException {
        if(validationTracker.getHasErrors()){
            return;
        }

        if(template.getNumberOfQuestions() < template.getQuestionsToPass()){
            validationTracker.recordError(messages.get("questions-to-pass-cannot-exceed-number-of-questions"));
        }

        if(((double)template.getQuestionsToPass())/template.getNumberOfQuestions() < 0.5){
            validationTracker.recordError(messages.get("questions-to-pass-must-exceed-50-percent-of-number-of-questions"));
        }

        if(template.getNumberOfQuestions() > testService.getTotalAvailableQuestions(template)){
            validationTracker.recordError(messages.get("number-of-questions-cannot-exceed-total"));
        }

    }

    public List<Course> getAllCourses(){
        return testService.getAllCourses();
    }

    @OnEvent(EventConstants.SUCCESS)
    Object save(){
        if(template.getId() == null){
            template.setCreationDate(new Date());
        }

        testService.persist(template);
        return TestTemplates.class;
    }

    public ValueEncoder<Course> getCourseValueEncoder(){
        return tapestryHelper.valueEncoder(Course.class);
    }
}
