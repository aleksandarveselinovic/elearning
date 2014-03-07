package elearning.base.bootstrap;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import javax.inject.Inject;

/**
 * Base class for BootstrapEditable.
 */
@SupportsInformalParameters
@Import(
    library = {
        "classpath:bootstrap-editable/js/bootstrap-editable.js"
    },
    stylesheet = {
        "classpath:bootstrap-editable/css/bootstrap-editable.css"
    }
)
public abstract class AbstractEditable implements ClientElement {

    @Parameter(value = "prop:componentResources.id", defaultPrefix = BindingConstants.LITERAL, allowNull = false)
    private String client;

    @Parameter(autoconnect = true, required = true)
    private Object value;

    @Parameter
    private Object[] context;

    @Parameter
    private boolean disabled;

    private String assignedClientId;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Inject
    private ComponentResources resources;

    @Inject
    private Request request;

    @Inject
    private TypeCoercer typeCoercer;

    @BeginRender
    void begin(MarkupWriter writer) {
        assignedClientId = javaScriptSupport.allocateClientId(resources);

        if (disabled) {
            writer.element("span");
        } else {
            Element element = writer.element("span",
                "id", getClientId(),
                "data-pk", "1",
                "data-name", getClientId(),
                "data-url", getPostbackLink());

            contributeDataParams(element);
        }

        resources.renderInformalParameters(writer);
    }

    protected void contributeDataParams(Element element) {

    }

    @AfterRender
    void after(MarkupWriter writer) {
        writer.end();

        if (!disabled) {
            javaScriptSupport.addScript(
                "jQuery('#%s').editable({" +
                    "success:function(data){ " +
                    "if(data.script){eval(data.script);}" +
                    "if(!data.success){return data.msg; }else return null;}" +
                    "});",
                getClientId());
        }
    }

    private String getPostbackLink() {
        return resources.createEventLink("postback", context).toAbsoluteURI();
    }

    @SuppressWarnings("unchecked")
    @OnEvent("postback")
    Object submit(@RequestParameter(value = "value", allowBlank = true) String value,
        @RequestParameter(value = "value[]", allowBlank = true) String valueList,
        EventContext context) {
        if (value != null) {
            this.value = toValue(value);
        }else if(valueList != null){
            this.value = toValue(valueList);
        }else {
            throw new RuntimeException("Required query parameter not found: value or value[]");
        }
        CaptureResultCallback<String> callback = new CaptureResultCallback<String>();
        boolean handled = resources.triggerContextEvent(EventConstants.SUBMIT, context, callback);

        JSONObject result = new JSONObject();

        if (handled) {
            String response = callback.getResult();
            if (response != null) {
                result.put("success", false);
                result.put("msg", response);
                return result;
            }
        }

        return result.put("success", true);
    }

    protected abstract Object toValue(String value);

    @Override
    public String getClientId() {
        return assignedClientId;
    }

    protected Object[] getContext() {
        return context;
    }
}
