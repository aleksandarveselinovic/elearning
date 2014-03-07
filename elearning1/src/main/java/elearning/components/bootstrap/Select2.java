package elearning.components.bootstrap;

import elearning.base.bootstrap.AbstractSelect2;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SelectModelVisitor;
import org.apache.tapestry5.annotations.BeforeRenderTemplate;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.data.BlankOption;
import org.apache.tapestry5.internal.TapestryInternalUtils;
import org.apache.tapestry5.internal.util.SelectModelRenderer;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.util.EnumSelectModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * An extended <code>&lt;select&gt;</code> component.
 */

public class Select2 extends AbstractSelect2 {

    private class Renderer extends SelectModelRenderer {

        public Renderer(MarkupWriter writer) {
            super(writer, encoder);
        }

        @Override
        protected boolean isOptionSelected(OptionModel optionModel, String clientValue) {
            System.err.println("Is selected : " + clientValue + " in " + selectedClientValues + " it is " + isSelected(clientValue));
            return isSelected(clientValue);
        }
    }

    /**
     * Whether the select will allow multiple selections.
     */
    @Parameter(value = "false", defaultPrefix = BindingConstants.LITERAL)
    private boolean multiple;

    /**
     * The model used to identify the option groups and options to be presented to the user. This can be generated
     * Enum values.
     */
    @Parameter(required = true, allowNull = false)
    private SelectModel model;

    /**
     * Controls whether an additional blank option is provided. The blank option precedes all other options and is never
     * selected. The value for the blank option is always the empty string, the label may be the blank string; the
     * label is from the blankLabel parameter (and is often also the empty string).
     */
    @Parameter(value = "auto", defaultPrefix = BindingConstants.LITERAL)
    private BlankOption blankOption;

    /**
     * The label to use for the blank option, if rendered. If not specified, the container's message catalog is
     * searched for a key, <code><em>id</em>-blanklabel</code>.
     */
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String blankLabel;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String zone;

    @Inject
    private ComponentResources resources;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    private List<String> selectedClientValues;

    Object defaultBlankLabel() {
        Messages containerMessages = resources.getContainerMessages();

        String key = resources.getId() + "-blanklabel";

        if (containerMessages.contains(key))
            return containerMessages.get(key);

        return null;
    }

    @SuppressWarnings("unchecked")
    SelectModel defaultModel() {
        Class valueType = resources.getBoundType("value");

        if (valueType == null)
            return null;

        if (Enum.class.isAssignableFrom(valueType))
            return new EnumSelectModel(valueType, resources.getContainerMessages());

        return null;
    }

    @Override
    protected void configureSelect2(JSONObject specs) {
        specs.put("autoComplete", false);
    }

    @Override
    protected void writeBeginTag(MarkupWriter writer) {
        writer.element("select", "name", getControlName(), "id", getClientId());

        if (multiple) {
            writer.attributes("multiple", "true");
        }

    }

    /**
     * Renders the options, including the blank option.
     */
    @BeforeRenderTemplate
    void options(MarkupWriter writer) {
        String selectedClientValue = tracker.getInput(this);

        // Use the value passed up in the form submission, if available.
        // Failing that, see if there is a current value (via the value parameter), and
        // convert that to a client value for later comparison.

        selectedClientValues = new ArrayList<String>();
        if (selectedClientValue != null) {
            for(String clientValue: TapestryInternalUtils.splitAtCommas(selectedClientValue)){
                selectedClientValues.add(clientValue);
            }
        }

        if (selectedClientValue == null && value != null) {
            selectedClientValues.addAll(toClient(value));
        }

        if (selectedClientValue != null) {
            selectedClientValues.add(selectedClientValue);
        }

        if (showBlankOption()) {
            writer.element("option", "value", "");
            writer.write(blankLabel);
            writer.end();
        }

        if (model != null) {
            SelectModelVisitor renderer = new Renderer(writer);
            model.visit(renderer);
        }
    }

    private boolean showBlankOption() {
        if (multiple) {
            return false;
        }

        switch (blankOption) {
            case ALWAYS:
                return true;

            case NEVER:
                return false;

            default:
                return !isRequired();
        }
    }

    private boolean isSelected(String clientValue) {
        return selectedClientValues.contains(clientValue);
    }

    @BeginRender
    void setupZone() {
        if (zone != null) {
            Link link = resources.createEventLink(CHANGE_EVENT);

            JSONObject spec = new JSONObject("selectId", getClientId(), "zoneId", zone, "url", link.toURI());

            javaScriptSupport.addInitializerCall("linkSelectToZone", spec);
        }

    }
}
