package elearning.components.bootstrap;

import elearning.base.bootstrap.AbstractDynamicSelect;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.services.ComponentDefaultProvider;

import javax.inject.Inject;

public class DynamicSelect extends AbstractDynamicSelect {

    @Inject
    private ComponentResources resources;

    @Inject
    private ComponentDefaultProvider defaultProvider;

    @Override
    protected void contributeDataParams(Element element) {
        super.contributeDataParams(element);
        element.attribute("data-type", "select");
    }


}
