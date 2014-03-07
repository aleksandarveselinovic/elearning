package elearning.pages.student;

import elearning.entities.Answer;
import elearning.entities.AnswerOption;
import elearning.entities.Question;
import elearning.entities.Test;
import elearning.pages.user.TestResult;
import elearning.pages.user.TestResults;
import elearning.services.TapestryHelper;
import elearning.services.TestService;
import elearning.services.TestSessionManager;
import java.text.DateFormat;
import java.util.Date;
import javax.inject.Inject;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.PageRenderLinkSource;

public class ShowQuestion {

    @PageActivationContext
    @Property
    private int answerIndex;

    @Property
    private AnswerOption answerOption;

    @Inject
    private TestSessionManager testSessionManager;

    @Inject
    private TestService testService;

    @Inject
    private TapestryHelper tapestryHelper;

    @Inject
    private PageRenderLinkSource pageRenderLinkSource;

    @Inject
    private Messages messages;

    @Inject
    private Block timeoutBlock;

    private enum Step {
        Previous, Next, Submit
    }

    ;

    private Step step;

    Object onActivate() {
        if (testSessionManager.getContext() == null) {
            return TestResults.class;
        }

        if (testSessionManager.timedOut()) {
            return submit();
        }

        if (answerIndex < 1) {
            answerIndex = 1;
        }

        if (answerIndex > testSessionManager.getTest().getAnswers().size()) {
            answerIndex = testSessionManager.getTest().getAnswers().size();
        }

        testService.viewed(getCurrentAnswer());

        return null;
    }

    private Object toResultPage(Test test) {
        return pageRenderLinkSource.createPageRenderLinkWithContext(TestResult.class, test);
    }

    void onPrepare() {
        for (AnswerOption answerOption : getCurrentAnswer().getOptions()) {
            AnswerOption selected = getFromQuestion(answerOption);
            selected.setSelected(true);
        }
    }

    private AnswerOption getFromQuestion(AnswerOption answerOption) {
        for (AnswerOption questionAnswerOption : getQuestion().getOptions()) {
            if (answerOption.getId().equals(questionAnswerOption.getId())) {
                return questionAnswerOption;
            }
        }

        //Should never happen.
        throw new RuntimeException("Answer Option not found");
    }

    public Answer getCurrentAnswer() {
        return testSessionManager.getCurrentAnswer(answerIndex);
    }

    public Question getQuestion() {
        return getCurrentAnswer().getQuestion();
    }

    public ValueEncoder<AnswerOption> getAnswerOptionEncoder() {
        return tapestryHelper.valueEncoder(AnswerOption.class);
    }

    public Test getTest() {
        return testSessionManager.getTest();
    }

    void onSelectedFromNext() {
        step = Step.Next;
    }

    void onSelectedFromPrevious() {
        step = Step.Previous;
    }

    void onSelectedFromSubmitTest() {
        step = Step.Submit;
    }

    Object onSuccess() {
        Answer answer = getCurrentAnswer();
        answer.getOptions().clear();
        for (AnswerOption answerOption : getQuestion().getOptions()) {
            if (answerOption.isSelected()) {
                answer.getOptions().add(answerOption);
            }
        }

        testService.persist(answer);

        switch (step) {
            case Previous:
                return goToQuestion(answerIndex - 1);

            case Next:
                if (!testSessionManager.timedOut()) {
                    return goToQuestion(answerIndex + 1);
                }
                // Let it go throw if it is timed out.

            case Submit:
            default:
                return submit();
        }
    }

    public boolean getHasPrevious() {
        return testSessionManager.hasPreviousAnswer(answerIndex);
    }

    public boolean getHasNext() {
        return testSessionManager.hasNextAnswer(answerIndex);
    }

    public DateFormat getDateFormat() {
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    }

    public Link goToQuestion(int questionIndex) {
        return pageRenderLinkSource.createPageRenderLinkWithContext(ShowQuestion.class, questionIndex);
    }

    public String getTimeRemaining() {
        long timeDiff = getTest().getEndTime().getTime() - new Date().getTime();
        return messages.format("time-remaining", timeDiff / 60000);
    }

    @OnEvent(EventConstants.PROGRESSIVE_DISPLAY)
    Object onProgressiveDisplay() {
        if (!testSessionManager.timedOut()) {
            return null;
        }

        return submit();
    }

    Object submit() {
        Test test = getTest();
        testSessionManager.submit();
        return toResultPage(test);
    }
}
