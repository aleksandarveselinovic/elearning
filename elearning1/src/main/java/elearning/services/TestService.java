package elearning.services;

import elearning.entities.Answer;
import elearning.entities.AnswerOption;
import elearning.entities.Course;
import elearning.entities.Question;
import elearning.entities.Test;
import elearning.entities.TestTemplate;
import elearning.entities.User;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;

import java.util.List;

public interface TestService {

    @CommitAfter
    void persist(Question question);

    @CommitAfter
    void persist(TestTemplate template);

    @CommitAfter
    void persist(Course course);

    @CommitAfter
    void delete(Question question);

    @CommitAfter
    void delete(TestTemplate template);

    @CommitAfter
    void delete(Course course);

    List<Course> getAllCourses();

    List<TestTemplate> getAllTemplates();

    List<Question> getAllQuestions();

    List<AnswerOption> getAnswerOptions(Question question);

    @CommitAfter
    void delete(AnswerOption answerOption);

    @CommitAfter
    Test createTestFromTemplate(TestTemplate template, User user);

    Test getTestById(long testId);

    @CommitAfter
    void persist(Answer answer);

    @CommitAfter
    void persist(Test test);

    boolean isQuestionInUse(Question question);

    boolean isCorrectAnswer(Answer answer);

    @CommitAfter
    void viewed(Answer answer);

    List<Test> getTests(TestTemplate template, User user);

    int getQuestionCount(Course course);

    @CommitAfter
    void persist(AnswerOption option);

    int getTotalAvailableQuestions(TestTemplate template);

}
