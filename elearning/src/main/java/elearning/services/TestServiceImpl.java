package elearning.services;

import elearning.entities.Answer;
import elearning.entities.AnswerOption;
import elearning.entities.Course;
import elearning.entities.Question;
import elearning.entities.Test;
import elearning.entities.TestStatus;
import elearning.entities.TestTemplate;
import elearning.entities.User;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TestServiceImpl implements TestService {

    private Session session;

    public TestServiceImpl(Session session) {
        this.session = session;
    }

    @Override
    public Test createTestFromTemplate(TestTemplate template, User user) {
        Test test = new Test();
        test.setTemplate(template);
        test.setStartTime(new Date());
        test.setTemplate(template);
        test.setEndTime(calculateEndTime(test.getStartTime(), template.getDuration()));
        test.setUser(user);
        test.setStatus(TestStatus.InProgress);

        for (Question question : getRandomQuestions(test.getTemplate().getCourses(), template.getNumberOfQuestions())) {
            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setViewed(false);
            test.addAnswer(answer);
        }

        session.save(test);
        return test;
    }

    @Override
    public Test getTestById(long testId) {
        return (Test) session.get(Test.class, testId);
    }

    @Override
    public void persist(Answer answer) {
        session.saveOrUpdate(answer);
    }

    @Override
    public void persist(Test test) {
        session.saveOrUpdate(test);
    }

    @Override
    public boolean isQuestionInUse(Question question) {
        return (Long) session.createCriteria(Test.class)
            .createCriteria("answers")
            .add(Restrictions.eq("question", question))
            .setProjection(Projections.rowCount())
            .uniqueResult() != 0L;
    }

    @Override
    public boolean isCorrectAnswer(Answer answer) {
        for (AnswerOption answerOption : answer.getQuestion().getOptions()) {
            if (answerOption.isCorrect() && !answer.getOptions().contains(answerOption)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void viewed(Answer answer) {
        answer.setViewed(true);
        session.saveOrUpdate(answer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Test> getTests(TestTemplate template, User user) {
        return session.createCriteria(Test.class)
            .add(Restrictions.eq("user", user))
            .add(Restrictions.eq("template", template))
            .list();
    }

    @Override
    public int getQuestionCount(Course course) {
        return (int)(long)(Long)session.createCriteria(Question.class)
            .add(Restrictions.eq("course", course))
            .setProjection(Projections.rowCount())
            .uniqueResult();
    }

    @Override
    public void persist(AnswerOption option) {
        session.saveOrUpdate(option);
    }

    @Override
    public int getTotalAvailableQuestions(TestTemplate template) {
        return (int)(long)(Long)session.createCriteria(Question.class)
            .add(Restrictions.in("course", template.getCourses()))
            .setProjection(Projections.rowCount())
            .add(Restrictions.eq("disabled", false))
            .uniqueResult();
    }

    private Date calculateEndTime(Date startTime, int duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.MINUTE, duration);
        return calendar.getTime();
    }

    @SuppressWarnings("unchecked")
    private List<Question> getRandomQuestions(List<Course> courses, int numberOfQuestions) {
        return session.createCriteria(Question.class)
            .add(Restrictions.in("course", courses))
            .add(Restrictions.eq("disabled", false))
            .add(Restrictions.sqlRestriction("1 = 1 order by rand()"))
            .setMaxResults(numberOfQuestions)
            .list();
    }

    @Override
    public void persist(Question question) {
        session.saveOrUpdate(question);
    }

    @Override
    public void persist(TestTemplate template) {
        session.saveOrUpdate(template);
    }

    @Override
    public void persist(Course course) {
        session.saveOrUpdate(course);
    }

    @Override
    public void delete(Question question) {
        session.delete(question);
    }

    @Override
    public void delete(TestTemplate template) {
        session.delete(template);
    }

    @Override
    public void delete(Course course) {
        session.delete(course);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Course> getAllCourses() {
        return session.createCriteria(Course.class).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TestTemplate> getAllTemplates() {
        return session.createCriteria(TestTemplate.class).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Question> getAllQuestions() {
        return session.createCriteria(Question.class).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AnswerOption> getAnswerOptions(Question question) {
        return session.createCriteria(AnswerOption.class)
            .add(Restrictions.eq("question", question))
            .addOrder(Order.asc("order"))
            .list();
    }

    @Override
    public void delete(AnswerOption answerOption) {
        session.delete(answerOption);
    }

}
