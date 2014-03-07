package elearning.components.bootstrap;

import elearning.base.bootstrap.AbstractDynamicSelect;
import org.apache.tapestry5.dom.Element;

public class TypeAhead extends AbstractDynamicSelect {

    @Override
    protected void contributeDataParams(Element element) {
        super.contributeDataParams(element);
        element.attribute("data-type", "typeahead");
    }


}
