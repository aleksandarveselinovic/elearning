package elearning.components.bootstrap;

import elearning.base.bootstrap.AbstractEditable;
import org.apache.tapestry5.dom.Element;

public class Text extends AbstractEditable {

    @Override
    protected void contributeDataParams(Element element){
        element.attribute("data-type", "text");
    }

    @Override
    protected Object toValue(String value) {
        return value;
    }

}
