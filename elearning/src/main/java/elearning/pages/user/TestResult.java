package elearning.pages.user;

import elearning.entities.Answer;
import elearning.entities.AnswerOption;
import elearning.entities.Test;
import elearning.services.TestService;
import elearning.services.UserManager;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;

import javax.inject.Inject;
import java.util.List;

/**
 * Page to show the test result details
 */
public class TestResult {

    @Inject
    private UserManager userManager;

    @PageActivationContext
    @Property
    private Test test;

    @Property
    private int answerIndex;

    @Property
    private int optionIndex;

    @Property
    private AnswerOption answerOption;

    @Property
    private Answer answer;

    @Inject
    private TestService testService;

    Object onActivate() {
        return test == null ? TestResults.class : null;
    }

    public int getPrettyIndex() {
        return answerIndex + 1;
    }

    public int getPrettyOptionIndex(){
        return optionIndex + 1;
    }

    public List<AnswerOption> getQuestionOptions() {
        return testService.getAnswerOptions(answer.getQuestion());
    }

    public boolean isSelected() {
        return answer.getOptions().contains(answerOption);
    }

    public String getCorrectOptionCssClass() {
        return answerOption.isCorrect() ? "correct-option alert alert-success" : "incorrect-option";
    }

    public String getCorrectAnswerCssClass(){
        return isCorrectAnswer() ? "correct-answer" : "incorrect-answer";
    }

    public boolean isCorrectAnswer(){
        return testService.isCorrectAnswer(answer);
    }

    public boolean getHasPassed(){
        return test.getCorrectAnswers() >= test.getTemplate().getQuestionsToPass();
    }
}
