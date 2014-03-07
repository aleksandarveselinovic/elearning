package elearning.base.bootstrap;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ComponentDefaultProvider;
import org.apache.tapestry5.util.EnumSelectModel;

import javax.inject.Inject;

public abstract class AbstractSelect extends AbstractEditable {

    @Parameter(required = true, allowNull = false)
    private SelectModel model;

    @Parameter(required = true, allowNull = false)
    private ValueEncoder encoder;

    @Inject
    private ComponentResources resources;

    @Inject
    private ComponentDefaultProvider defaultProvider;

    @SuppressWarnings("unchecked")
    ValueEncoder defaultEncoder() {
        return defaultProvider.defaultValueEncoder("value", resources);
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
    protected void contributeDataParams(Element element) {
        element.attribute("data-source", getSource());
    }

    @SuppressWarnings("unchecked")
    public String getSource() {
        JSONArray source = new JSONArray();

        for (OptionModel option : model.getOptions()) {
            source.put(new JSONObject()
                .put("value", encoder.toClient(option.getValue()))
                .put("text", option.getLabel()));
        }

        return source.toCompactString();
    }

    @Override
    protected Object toValue(String value) {
        return encoder.toValue(value);
    }

    protected ValueEncoder<?> getEncoder() {
        return encoder;
    }

}
