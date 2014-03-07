package elearning.services;

import elearning.TestContext;
import elearning.entities.Answer;
import elearning.entities.Test;
import elearning.entities.TestTemplate;

public interface TestSessionManager {

    Test getTest();

    void create(TestTemplate template);

    Answer getCurrentAnswer(int answerIndex);

    boolean hasNextAnswer(int answerIndex);

    boolean hasPreviousAnswer(int answerIndex);

    void submit();

    TestContext getContext();

    boolean timedOut();

}
