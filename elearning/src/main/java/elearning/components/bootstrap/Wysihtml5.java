package elearning.components.bootstrap;

import elearning.base.bootstrap.AbstractEditable;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.json.JSONObject;

@Import(library = {
    "classpath:bootstrap-editable/js/bootstrap-editable.js",
    "classpath:/bootstrap-wysihtml5/wysihtml5-0.3.0.min.js",
    "classpath:/bootstrap-wysihtml5/bootstrap-wysihtml5-0.0.2.min.js",
    "classpath:/bootstrap-editable/js/wysihtml5.js"
},
    stylesheet = "classpath:/bootstrap-wysihtml5/bootstrap-wysihtml5-0.0.2.css")
public class Wysihtml5 extends AbstractEditable {

    @Parameter(value = "literal:{stylesheets:false}", allowNull = false)
    private JSONObject params;

    @Override
    protected void contributeDataParams(Element element) {
        element.attribute("data-type", "wysihtml5");
        element.attribute("wysihtml5", params.toCompactString());
    }

    @Override
    protected Object toValue(String value) {
        return value;
    }

}
