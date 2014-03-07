package elearning.components.bootstrap;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.alerts.Alert;
import org.apache.tapestry5.alerts.AlertStorage;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import javax.inject.Inject;

/**
 * This component shows notifications .
 */
public class Notifications {
    @SessionState(create = false)
    @Property
    private AlertStorage storage;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Property
    private Alert alert;

    @Parameter(defaultPrefix = BindingConstants.LITERAL, value = "false")
    private boolean block;

    @AfterRender
    void addJavaScript() {
        javaScriptSupport.addScript("$('.alert-message').alert();");
    }

    public String getCssClass() {
        String cssClass = "alert-success";

        switch (alert.severity) {
            case INFO:
                cssClass = "alert-info";
                break;

            case ERROR:
                cssClass = "alert-error";
                break;

            case WARN:
                cssClass = "alert-error";
                break;
        }

        return cssClass;
    }

}
