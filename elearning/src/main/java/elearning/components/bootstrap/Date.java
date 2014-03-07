package elearning.components.bootstrap;

import elearning.base.bootstrap.AbstractEditable;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.dom.Element;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

public class Date extends AbstractEditable {

    @Parameter(value = "yyyy-MM-dd", defaultPrefix = BindingConstants.LITERAL,
        required = true, allowNull = false)
    private DateFormat format;

    @Parameter(value = "yyyy-mm-dd", defaultPrefix = BindingConstants.LITERAL,
        required = true, allowNull = false)
    private String clientFormat;

    @Inject
    private Locale locale;

    @Override
    protected Object toValue(String value) {
        try {
            return format.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void contributeDataParams(Element element) {
        element.attribute("data-type", "date");
        element.attribute("data-format", clientFormat);
    }

}
