package elearning.components.bootstrap;

import elearning.base.bootstrap.AbstractEditable;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.dom.Element;

public class HtmlType extends AbstractEditable {

    @Parameter(required = true, allowNull = false)
    private String dataType;

    @Override
    protected void contributeDataParams(Element element){
        element.attribute("data-type", dataType);
    }

    @Override
    protected Object toValue(String value) {
        return value;
    }

}
