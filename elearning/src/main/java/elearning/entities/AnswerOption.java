package elearning.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Answer options available to a {@link Question}.
 */
@Entity
public class AnswerOption {

    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "text")
    private String text;

    private boolean isCorrect;

    @ManyToOne
    @NotNull
    private Question question;

    @Column(name = "option_order")
    private int order;

    @Transient
    private boolean selected;

    /**
     * Primary key
     * @return primary key
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Option text in HTML format.
     * @return text
     */
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Is this option correct.
     * @return true if this option is correct.
     */
    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    /**
     * Question to which this option belongs.
     * @return question
     */
    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    /**
     * It is used for sorting the options before presenting it.  A smaller
     * number will put it at the top (ascending order).
     * @return order
     */
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * It is used in the presentation layer by tapestry
     * for selecting options.
     * @return which associated checkbox is selected.
     */
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || !(other instanceof AnswerOption)) {
            return false;
        }

        AnswerOption that = (AnswerOption) other;
        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
