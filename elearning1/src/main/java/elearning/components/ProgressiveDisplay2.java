package elearning.components;

import javax.inject.Inject;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(library = "ProgressiveDisplay2.js")
public class ProgressiveDisplay2 implements ClientElement {

    private static final String PROGRESSIVE_DISPLAY_INTERNAL =
        EventConstants.PROGRESSIVE_DISPLAY + "2";

    @Parameter(value = "2", defaultPrefix = BindingConstants.LITERAL)
    private int period;

    @Parameter
    private Object[] context;

    @Parameter(value = "prop:componentResources.id", defaultPrefix = BindingConstants.LITERAL,
        allowNull = false)
    private String clientId;

    private String assignedClientId;

    @SuppressWarnings("UnusedDeclaration")
    Object[] defaultContext() {
        return new Object[]{};
    }

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Inject
    private ComponentResources resources;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    @InjectComponent
    private Zone zone;

    @SetupRender
    void assignClientId() {
        assignedClientId = javaScriptSupport.allocateClientId(clientId);
    }

    @AfterRender
    void addJavaScript() {
        JSONObject specs = getArguments(getClientId(), context);
        javaScriptSupport.addInitializerCall("ProgressiveDisplay2", specs);
    }

    private JSONObject getArguments(String clientId, Object[] context) {
        JSONObject specs = new JSONObject();
        specs.put("url", createUpdateURI(context));
        specs.put("period", period);
        specs.put("zoneId", clientId);
        return specs;
    }

    private String createUpdateURI(Object... context) {
        return resources.createEventLink(PROGRESSIVE_DISPLAY_INTERNAL, context)
            .toURI();
    }

    @OnEvent(PROGRESSIVE_DISPLAY_INTERNAL)
    private Object update(final EventContext context, @RequestParameter("zoneId") final String zoneId) {
        CaptureResultCallback<Object> callback = new CaptureResultCallback<Object>();

        boolean handled = resources.triggerContextEvent(
            EventConstants.PROGRESSIVE_DISPLAY,
            context, callback);

        if (handled && callback.getResult() == null) {
            ajaxResponseRenderer.addCallback(new JavaScriptCallback() {

                @Override
                public void run(JavaScriptSupport javaScriptSupport) {
                    javaScriptSupport.addInitializerCall("ProgressiveDisplay2",
                        getArguments(zoneId, context.toStrings()));
                }

            });

            return zone.getBody();
        }

        return callback.getResult();
    }

    @Override
    public String getClientId() {
        return assignedClientId;
    }

}
