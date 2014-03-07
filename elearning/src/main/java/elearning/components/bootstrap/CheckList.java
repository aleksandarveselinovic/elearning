package elearning.components.bootstrap;

import elearning.base.bootstrap.AbstractSelect;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.internal.TapestryInternalUtils;

import java.util.ArrayList;
import java.util.List;

public class CheckList extends AbstractSelect {

    @Override
    protected void contributeDataParams(Element element) {
        super.contributeDataParams(element);
        element.attribute("data-type", "checklist");
        element.attribute("data-separator", ",");
    }

    @Override
    protected Object toValue(String valueList) {
        List<Object> values = new ArrayList<Object>();

        for (String value : TapestryInternalUtils.splitAtCommas(valueList)) {
            values.add(getEncoder().toValue(value));
        }

        return values;
    }

}
