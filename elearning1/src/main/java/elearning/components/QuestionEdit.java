package elearning.components;

import elearning.Symbols;
import elearning.entities.AnswerOption;
import elearning.entities.Course;
import elearning.entities.Question;
import elearning.services.TestService;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ComponentDefaultProvider;
import org.hibernate.Session;

import javax.inject.Inject;

public class QuestionEdit {

    @Property
    @Parameter
    private Question question;

    @Parameter
    @Property
    private Course course;

    @Property
    private AnswerOption answerOption;

    @Inject
    private TestService testService;

    @Inject
    private ComponentResources resources;

    @Inject
    private ComponentDefaultProvider componentDefaultProvider;

    @Inject
    private Session session;

    @Inject
    private Messages messages;

    @InjectComponent
    private Zone questionZone;

    @Inject
    @Symbol(Symbols.DEFAULT_OPTION_COUNT)
    private int defaultOptionCount;

    void onPrepare() {
        if (question == null) {
            question = new Question();
        }
    }

    void onPrepareForSubmit(Question question, Course course) {
        if (question == null) {
            this.question = new Question();
        } else {
            this.question = question;
        }
        this.course = course;
    }

    Object onSuccess() {
        question.setCourse(course);
        question.setDisabled(true);

        for(int i = 0; i < defaultOptionCount; ++i){
            question.addAnswerOption(new AnswerOption());
        }
        testService.persist(question);

        return resources.getPageName();
    }

    Object onFailure() {
        return questionZone.getBody();
    }

}
