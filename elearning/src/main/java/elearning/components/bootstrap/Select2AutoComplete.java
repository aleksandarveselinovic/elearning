package elearning.components.bootstrap;

import elearning.base.bootstrap.AbstractSelect2;
import elearning.services.LabelProvider;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.func.F;
import org.apache.tapestry5.func.Worker;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class Select2AutoComplete extends AbstractSelect2 {

    public static final String QUERY = "query";

    @Parameter
    private LabelProvider<Object> labelProvider;

    @Parameter
    private String query;

    /**
     * Whether the select will allow multiple selections.
     */
    @Parameter(value = "false", defaultPrefix = BindingConstants.LITERAL)
    private boolean multiple;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String zone;

    @Inject
    private ComponentResources resources;

    LabelProvider<Object> defaultLabelProvider() {
        return new LabelProvider<Object>() {
            @Override
            public String getLabel(Object value) {
                return value.toString();
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void configureSelect2(JSONObject specs) {
        specs.put("callbackURL", getCallback());
        specs.put("autoComplete", true);
        specs.put("multiple", multiple);

        String selectedClientValue = tracker.getInput(this);

        List<Object> selectedValues = null;
        if (selectedClientValue != null) {
            selectedValues = toValue(selectedClientValue);
        } else if (value != null) {
            selectedValues = value instanceof List ? (List<Object>) value : Arrays.asList(value);
        }

        JSONArray array = toJSONArray(selectedValues);
        if (selectedValues != null && array.length() != 0) {
            specs.put("initialSelection",
                multiple ? array : array.get(0));
        }

    }

    @Override
    protected void writeBeginTag(MarkupWriter writer) {

    }

    @AfterRender
     void writeHiddenTag(MarkupWriter writer) {
        writer.element("input", "name", getControlName(),
            "type", "hidden", "id", getClientId());
        String selectedClientValue = tracker.getInput(this);

        if (selectedClientValue == null) {
            selectedClientValue = value == null ? null : InternalUtils.join(toClient(value));
        }

        if (selectedClientValue != null) {
            writer.attributes("value", selectedClientValue);
        }

    }

    private String getCallback() {
        return resources.createEventLink(QUERY, context).toAbsoluteURI();
    }

    @OnEvent(QUERY)
    JSONObject query(
        @RequestParameter(value = "query", allowBlank = true)
        String query,
        EventContext context) {
        this.query = query;
        CaptureResultCallback<List<Object>> callback = new CaptureResultCallback<List<Object>>();
        boolean handled = resources.triggerContextEvent(
            EventConstants.PROVIDE_COMPLETIONS, context, callback);
        if (!handled) {
            throw new RuntimeException(String.format("Event '%s' must be handled", query));
        }

        final JSONArray data = toJSONArray(callback.getResult());

        JSONObject result = new JSONObject();
        result.put("data", data);
        return result;
    }

    private JSONArray toJSONArray(List<Object> result) {
        final JSONArray data = new JSONArray();

        if (result == null) {
            return data;
        }

        F.flow(result).each(new Worker<Object>() {
            @Override
            public void work(Object element) {
                JSONObject row = new JSONObject("id", encoder.toClient(element),
                    "text", labelProvider.getLabel(element));

                data.put(row);
            }
        });

        return data;
    }

}
