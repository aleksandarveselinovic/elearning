package elearning.components.bootstrap;

import elearning.base.bootstrap.AbstractSelect;
import org.apache.tapestry5.dom.Element;

public class Select extends AbstractSelect {

    @Override
    protected void contributeDataParams(Element element) {
        super.contributeDataParams(element);
        element.attribute("data-type", "select");
    }

}
