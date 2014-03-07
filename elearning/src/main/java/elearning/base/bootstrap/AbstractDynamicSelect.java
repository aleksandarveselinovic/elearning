package elearning.base.bootstrap;

import elearning.Events;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ComponentDefaultProvider;

import javax.inject.Inject;

public abstract class AbstractDynamicSelect extends AbstractEditable {

    @Parameter(required = true, allowNull = false)
    private ValueEncoder encoder;

    @Inject
    private TypeCoercer typeCoercer;

    @Inject
    private ComponentResources resources;

    @Inject
    private ComponentDefaultProvider defaultProvider;

    @SuppressWarnings("unchecked")
    ValueEncoder defaultEncoder() {
        return defaultProvider.defaultValueEncoder("value", resources);
    }

    @Override
    protected void contributeDataParams(Element element) {
        element.attribute("data-source", getSourceCallback());
    }

    private String getSourceCallback() {
        return resources.createEventLink("populate", getContext()).toURI();
    }

    @SuppressWarnings("unchecked")
    @OnEvent("populate")
    JSONArray populate(EventContext context) {
        CaptureResultCallback<SelectModel> callback = new CaptureResultCallback<SelectModel>();
        boolean handled = resources.triggerContextEvent(
            Events.POPULATE_OPTIONS, context, callback);

        if (!handled) {
            throw new RuntimeException(String.format("Event '%s' must be handled",
                Events.POPULATE_OPTIONS));
        }

        JSONArray result = new JSONArray();
        for (OptionModel optionModel : typeCoercer.coerce(callback.getResult(), SelectModel.class).getOptions()) {
            result.put(new JSONObject().put("value", encoder.toClient(optionModel.getValue()))
                .put("text", optionModel.getLabel()));
        }

        return result;
    }

    @Override
    protected Object toValue(String value) {
        return encoder.toValue(value);
    }

    protected ValueEncoder<?> getEncoder() {
        return encoder;
    }

}
