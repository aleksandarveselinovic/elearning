package elearning.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A test is generated from a {@link TestTemplate}. Random number of questions equal to
 * {@link TestTemplate#numberOfQuestions} are added to the test as {@link Answer}s.
 * {@link TestTemplate#duration} is used for setting the time period of  the test.
 */
@Entity
public class Test {

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<Answer>();

    @ManyToOne
    @NotNull
    private TestTemplate template;

    @ManyToOne
    @NotNull
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date endTime;

    private int correctAnswers;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TestStatus status;

    /**
     * Primary key
     *
     * @return primary key
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * {@link Answer}
     *
     * @return list of answers
     */
    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    /**
     * Get {@link TestTemplate} from which it was generated
     *
     * @return template
     */
    public TestTemplate getTemplate() {
        return template;
    }

    public void setTemplate(TestTemplate template) {
        this.template = template;
    }

    /**
     * Get user for whom the test was generated.
     * @return user
     */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Add a new answer to the test.
     * @param answer answer
     */
    public void addAnswer(Answer answer) {
        answers.add(answer);
        answer.setTest(this);
    }

    /**
     * Start time of the test.
     * @return start time.
     */
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * End time of the test.
     * @return end time.
     */
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Get number of correct answers.
     * @return correct answers
     */
    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public TestStatus getStatus() {
        return status;
    }

    public void setStatus(TestStatus status) {
        this.status = status;
    }
}
