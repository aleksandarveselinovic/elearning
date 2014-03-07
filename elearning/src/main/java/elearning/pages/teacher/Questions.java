package elearning.pages.teacher;

import elearning.entities.AnswerOption;
import elearning.entities.Course;
import elearning.entities.Question;
import elearning.services.TapestryHelper;
import elearning.services.TestService;
import elearning.utils.CriteriaConfigurer;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;

public class Questions {

    @Property
    private Question question;

    @Property
    private String text;

    @Property
    private AnswerOption answerOption;

    @PageActivationContext
    @Property
    private Course course;

    @Inject
    private TestService testService;

    @Inject
    private TapestryHelper tapestryHelper;

    @InjectComponent
    private Zone questionZone;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    public GridDataSource getAllQuestions() {
        return tapestryHelper.getDataSource(Question.class, new CriteriaConfigurer() {
            @Override
            public void configure(Criteria criteria) {
                if(course != null){
                    criteria.add(Restrictions.eq("course", course));
                }
                criteria.addOrder(Order.desc("id"));
            }
        });
    }

    @OnEvent("delete")
    void delete(Question question) {
        testService.delete(question);
    }

    @OnEvent("disable")
    Object disable(Question question) {
        this.question = question;
        question.setDisabled(true);
        testService.persist(question);
        return questionZone.getBody();
    }

    @OnEvent("enable")
    Object enable(Question question) {
        this.question = question;
        if(getHasCorrectAnswer()){
            System.err.println("Yes going ahead!");
            question.setDisabled(false);
            testService.persist(question);
        }else {
            System.err.println("No answer is correct!");
        }
        return questionZone.getBody();

    }

    public boolean getHasCorrectAnswer(){
        boolean correct = false;

        for(AnswerOption answerOption: question.getOptions()){
            if(answerOption.isCorrect()){
                correct = true;
                break;
            }
        }

        return correct;
    }

    @OnEvent("deleteAnswerOption")
    Object deleteAnswerOption(AnswerOption answerOption){
        this.question = answerOption.getQuestion();
        testService.delete(answerOption);
        return questionZone.getBody();
    }

    @OnEvent("addAnswerOption")
    Object addAnswerOption(Question question){
        this.question = question;
        AnswerOption option = new AnswerOption();
        question.addAnswerOption(option);
        testService.persist(question);
        testService.persist(option);
        return questionZone.getBody();
    }

    void onSubmitFromQuestionText(Question question){
        question.setText(text);
        testService.persist(question);
    }

    void onSubmitFromAnswerText(AnswerOption answerOption){
        answerOption.setText(text);
        testService.persist(answerOption);
    }

    void onSelectedFromCorrect(AnswerOption answerOption){
        answerOption.setCorrect(!answerOption.isCorrect());
        this.answerOption = answerOption;
        this.question = answerOption.getQuestion();
        this.question.setDisabled(!getHasCorrectAnswer());
        testService.persist(question);
        testService.persist(answerOption);
        ajaxResponseRenderer.addRender(questionZone);
    }

    public boolean isQuestionInUse() {
        return testService.isQuestionInUse(question);
    }

    public String getDisabledCss(){
        return question.isDisabled() ? "muted": "";
    }
}
