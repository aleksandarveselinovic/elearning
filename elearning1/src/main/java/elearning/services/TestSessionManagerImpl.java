package elearning.services;

import elearning.TestContext;
import elearning.entities.Answer;
import elearning.entities.Test;
import elearning.entities.TestStatus;
import elearning.entities.TestTemplate;
import java.util.Date;
import org.apache.tapestry5.services.ApplicationStateManager;

public class TestSessionManagerImpl implements TestSessionManager {

    private ApplicationStateManager stateManager;

    private TestService testService;

    private UserManager userManager;

    public TestSessionManagerImpl(ApplicationStateManager stateManager, TestService testService,
        UserManager userManager) {
        this.stateManager = stateManager;
        this.testService = testService;
        this.userManager = userManager;
    }

    @Override
    public Test getTest() {
        TestContext context = stateManager.get(TestContext.class);
        return testService.getTestById(context.getTestId());
    }

    @Override
    public void submit() {
        int correctAnswers = 0;
        for (Answer answer : getTest().getAnswers()) {
            if (testService.isCorrectAnswer(answer)) {
                correctAnswers += 1;
            }
        }

        if(timedOut()){
            getTest().setStatus(TestStatus.TimedOut);
        }else {
            getTest().setStatus(TestStatus.Finished);
        }

        getTest().setCorrectAnswers(correctAnswers);
        testService.persist(getTest());
        stateManager.set(TestContext.class, null);
    }

    @Override
    public TestContext getContext() {
        return stateManager.getIfExists(TestContext.class);
    }

    @Override
    public boolean timedOut() {
        return new Date().after(getTest().getEndTime());
    }

    @Override
    public void create(TestTemplate template) {
        Test test = testService.createTestFromTemplate(template, userManager.getCurrentUser());
        stateManager.set(TestContext.class, new TestContext(test.getId()));
    }

    @Override
    public Answer getCurrentAnswer(int answerIndex) {
        return getTest().getAnswers().get(answerIndex - 1);
    }

    @Override
    public boolean hasNextAnswer(int answerIndex) {
        return answerIndex < getTest().getAnswers().size();
    }

    @Override
    public boolean hasPreviousAnswer(int answerIndex) {
        return answerIndex > 1;
    }

}
