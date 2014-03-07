package elearning.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 200)
    @NotNull
    private String title;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<Question>();

    public Long getId() {
        return id;
    }

    /**
     * Primary key
     * @param id primary key
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Set title
     * @param title title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString(){
        return title;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || !(other instanceof Course)) {
            return false;
        }

        Course course = (Course) other;

        return !(id != null ? !id.equals(course.id) : course.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
