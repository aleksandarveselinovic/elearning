package elearning.base.bootstrap;

import org.apache.tapestry5.Binding;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.FieldValidationSupport;
import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.base.AbstractField;
import org.apache.tapestry5.internal.TapestryInternalUtils;
import org.apache.tapestry5.internal.util.CaptureResultCallback;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ComponentDefaultProvider;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for components based on <a href='http://ivaynberg.github.com/select2'>Select2</a>.
 *
 * Parameters can be directly passed to the script using {@link #params} parameter.
 */
@SuppressWarnings("rawtypes")
@Events(EventConstants.PROVIDE_COMPLETIONS)
@SupportsInformalParameters
@Import(library = {"classpath:select2/select2.js", "Select2.js"},
    stylesheet = "classpath:select2/select2.css")
public abstract class AbstractSelect2 extends AbstractField {

    public static final String CHANGE_EVENT = "change";

    /**
     * Value to be set
     */
    @Parameter(required = true, autoconnect = true)
    protected Object value;


    /**
     * A ValueEncoder used to convert the server-side object provided by the
     * "value" parameter into a unique client-side string (typically an ID) and
     * back. Note: this parameter may be OMITTED if Tapestry is configured to
     * provide a ValueEncoder automatically for the type of property bound to
     * the "value" parameter.
     *
     * @see org.apache.tapestry5.services.ValueEncoderSource
     */
    @Parameter
    protected ValueEncoder<Object> encoder;

    /**
     * Context to be passed in case of an autoComplete callback.
     */
    @Parameter(value = "[]")
    protected Object[] context;

    /**
     * Performs input validation on the value supplied by the user in the form submission.
     */
    @Parameter(defaultPrefix = BindingConstants.VALIDATE)
    private FieldValidator<Object> validate;

    /**
     * Parameters to be passed directly to the select2 script.
     */
    @Parameter(value = "literal:{}", allowNull = false)
    private JSONObject params;

    @Inject
    private FieldValidationSupport fieldValidationSupport;

    @Environmental
    private FormSupport formSupport;

    @Inject
    private JavaScriptSupport javascriptSupport;

    @Inject
    private Request request;

    @Inject
    private Logger logger;

    @Inject
    private ComponentResources resources;

    @Inject
    private ComponentDefaultProvider defaultProvider;

    @Environmental
    protected ValidationTracker tracker;

    @Inject
    private Messages messages;

    @SuppressWarnings("unchecked")
    ValueEncoder defaultEncoder() {
        return defaultProvider.defaultValueEncoder("value", resources);
    }

    /**
     * Computes a default value for the "validate" parameter using {@link org.apache.tapestry5.services
     * .FieldValidatorDefaultSource}.
     */
    Binding defaultValidate() {
        return defaultProvider.defaultValidatorBinding("value", resources);
    }


    @BeginRender
    void begin(MarkupWriter writer) {

        writeBeginTag(writer);

        putPropertyNameIntoBeanValidationContext("value");

        validate.render(writer);

        removePropertyNameFromBeanValidationContext();

        renderInformalParameters(writer);

        decorateInsideField();

        setupSelect2();
    }

    /**
     * Only render variables which do not start with <code>data-</code>
     */
    private void renderInformalParameters(MarkupWriter writer) {
        for (String parameter : resources.getInformalParameterNames()) {
            String value = resources.getInformalParameter(parameter, String.class);
            if (parameter.startsWith("data-")) {
                params.put(parameter.substring(5), value);
            } else {
                writer.attributes(parameter, value);
            }
        }
    }

    private void setupSelect2() {
        JSONObject specs = new JSONObject();
        specs.put("id", getClientId());
        params.put("allowClear", !isRequired());
        specs.put("params", params);
        addMessages(specs);
        configureSelect2(specs);
        javascriptSupport.addInitializerCall("select2", specs);
    }

    private void addMessages(JSONObject specs) {
        specs.put("formatNoMatches", messages.get("select2.no-matches"));
        specs.put("formatSearching", messages.get("select2.searching"));
        specs.put("formatInputTooShort", messages.get("select2.input-too-short"));
        specs.put("formatSelectionTooBig", messages.get("select2.selection-too-big"));
        specs.put("formatLoadMore", messages.get("select2.load-more"));
    }

    protected abstract void configureSelect2(JSONObject specs);

    abstract protected void writeBeginTag(MarkupWriter writer);

    void afterRender(MarkupWriter writer) {
        writer.end();
    }

    @SuppressWarnings("unchecked")
    protected List<String> toClient(Object value) {
        List<String> values = new ArrayList<String>();

        if (value instanceof List) {
            for (Object item : (List<Object>) value) {
                values.add(encoder.toClient(item));
            }
        } else {
            values.add(encoder.toClient(value));
        }

        return values;
    }

    Object onChange(@RequestParameter(value = "t:selectvalue", allowBlank = true)
    final String selectValue) {
        final Object newValue = toValue(selectValue);

        CaptureResultCallback<Object> callback = new CaptureResultCallback<Object>();

        this.resources.triggerEvent(EventConstants.VALUE_CHANGED, new Object[]
            {newValue}, callback);

        this.value = newValue;

        return callback.getResult();
    }

    protected List<Object> toValue(String clientValue) {
        List<String> submittedValues;
        if (clientValue == null) {
            submittedValues = new ArrayList<String>();
        } else {
            submittedValues = Arrays.asList(TapestryInternalUtils.splitAtCommas(clientValue));
        }

        List<Object> values = new ArrayList<Object>();

        for (String submittedValue : submittedValues) {
            values.add(encoder.toValue(submittedValue));
        }

        return values;
    }

    @Override
    public boolean isRequired() {
        return validate.isRequired();
    }

    @Override
    protected void processSubmission(String controlName) {
        String[] submittedValues = request.getParameters(controlName);

        List<String> submittedValueList;
        if (submittedValues == null) {
            submittedValueList = new ArrayList<String>();
        } else {
            submittedValueList = Arrays.asList(submittedValues);
        }

        tracker.recordInput(this, InternalUtils.join(submittedValueList));

        List<Object> selectedValues = toValue(InternalUtils.join(submittedValueList));

        putPropertyNameIntoBeanValidationContext("value");

        try {
            fieldValidationSupport.validate(selectedValues, resources, validate);
            value = resources.getBoundType("value") == List.class ? selectedValues :
                selectedValues.size() == 0 ? null : selectedValues.get(0);
        } catch (ValidationException ex) {
            tracker.recordError(this, ex.getMessage());
        }

        removePropertyNameFromBeanValidationContext();
    }

}
