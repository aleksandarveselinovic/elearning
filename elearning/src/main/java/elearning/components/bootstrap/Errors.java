package elearning.components.bootstrap;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.internal.InternalMessages;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import javax.inject.Inject;
import java.util.List;

/**
 * Bootstrap based error messages
 */
@SupportsInformalParameters
public class Errors implements ClientElement {
    @Parameter(value = "message:default-banner")
    private String banner;

    @Environmental(false)
    private ValidationTracker tracker;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Parameter(value = "fade in", defaultPrefix = BindingConstants.LITERAL, allowNull = false)
    private String _class;

    @Parameter(value = "prop:componentResources.id",
            defaultPrefix = BindingConstants.LITERAL,
            allowNull = false)
    private String clientId;

    private String assignedClientId;

    @SetupRender
    void setup() {
        assignedClientId = javaScriptSupport.allocateClientId(clientId);
    }

    @BeginRender
    void render(MarkupWriter writer) {
        if (tracker == null) {
            throw new RuntimeException(InternalMessages.encloseErrorsInForm());
        }

        if (!tracker.getHasErrors()) {
            return;
        }

        writer.element("div",
                "id", getClientId(),
                "class", "alert alert-error error " + _class +
                " alert-block");
        writer.element("a",
                "href", "#",
                "class", "close",
                "data-dismiss", "alert");
        writer.write("x");
        writer.end();

        List<String> errors = tracker.getErrors();
        if (!errors.isEmpty()) {
            writer.element("p");
            writer.element("strong");
            writer.write(banner);
            writer.end();
            writer.end();

            writer.element("ul");

            for (String error : errors) {
                writer.element("li");
                writer.write(error);
                writer.end();
            }

            writer.end();
        }
        writer.end();
    }

    @Override
    public String getClientId() {
        return assignedClientId;
    }
}
