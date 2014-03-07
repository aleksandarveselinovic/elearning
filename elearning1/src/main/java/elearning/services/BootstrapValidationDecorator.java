package elearning.services;

import org.apache.tapestry5.BaseValidationDecorator;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.services.Environment;

public class BootstrapValidationDecorator extends BaseValidationDecorator {

    private Environment environment;

    private MarkupWriter writer;

    public BootstrapValidationDecorator(Environment environment, MarkupWriter writer) {
        this.environment = environment;
        this.writer = writer;
    }

    @Override
    public void insideField(Field field) {
        if (inError(field)) {
            addErrorClass();
        }
    }

    private void addErrorClass() {
        writer.getElement().addClassName("error");
        writer.getElement().getContainer().getContainer().addClassName("error");
    }

    @Override
    public void afterField(Field field) {
        if (field.isRequired()) {
            addRequiredClass();
        }

        if (inError(field)) {
            addError(field);
        }
    }

    private void addRequiredClass() {
        writer.getElement().addClassName("required");
    }

    private void addError(Field field) {
        writer.element("span", "class", "help-inline");
        writer.write(getValidationTracker().getError(field));
        writer.end();
    }

    private boolean inError(Field field) {
        ValidationTracker tracker = getValidationTracker();
        return tracker.inError(field);
    }

    private ValidationTracker getValidationTracker() {
        return environment.peekRequired(ValidationTracker.class);
    }

}
