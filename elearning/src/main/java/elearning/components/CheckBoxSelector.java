package elearning.components;


import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.EventLink;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

public class CheckBoxSelector implements ClientElement {

    @Parameter(
        value = "prop:componentResources.id",
        defaultPrefix = BindingConstants.LITERAL,
        allowNull = false)
    private String clientId;

    @SuppressWarnings("unused")
    @Component(id = "link", publishParameters = "disabled, anchor, context")
    private EventLink link;

    @Parameter(value = "selected", defaultPrefix = BindingConstants.LITERAL, allowNull = false)
    private String event;

    @SuppressWarnings("unused")
    @Parameter(required = true)
    @Property(write = false)
    private boolean checked;

    private String assignedClientId;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Inject
    private ComponentResources resources;

    @InjectComponent
    private Zone zone;

    @SetupRender
    void allocateClientId() {
        assignedClientId = javaScriptSupport.allocateClientId(clientId);
    }

    Object onClick(EventContext context) {
        CaptureResultCallback<Object> callback = new CaptureResultCallback<Object>();
        resources.triggerContextEvent(event, context, callback);
        return zone.getBody();
    }

    @Override
    public String getClientId() {
        return assignedClientId;
    }

    public String getZoneId() {
        return getClientId() + "_zone";
    }

}
