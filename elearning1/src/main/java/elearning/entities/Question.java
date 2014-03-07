package elearning.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * A question with associated {@link AnswerOption}s. A question can be
 * deleted if it has not been used in a {@link Test}.
 *
 * A question can be disabled in which case it will not be included in a newly
 * generated test.
 *
 * @see Test
 */
@Entity
public class Question {

    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "text")
    private String text;

    @ManyToOne
    private Course course;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<AnswerOption> options = new ArrayList<AnswerOption>();

    private boolean disabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<AnswerOption> getOptions() {
        return options;
    }

    public void setOptions(List<AnswerOption> options) {
        this.options = options;
    }

    public void addAnswerOption(AnswerOption answerOption) {
        options.add(answerOption);
        answerOption.setQuestion(this);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || !(other instanceof Question)) {
            return false;
        }

        Question question = (Question) other;

        return !(id != null ? !id.equals(question.id) : question.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }
}
