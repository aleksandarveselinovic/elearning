(function ($) {
    $.extend(Tapestry.Initializer, {
        select2: function (specs) {
            if (!specs.params) {
                specs.params = {};
            }

            specs.params.width = "element";

            if (specs.multiple) {
                specs.params.multiple = specs.multiple;
            }

            if (specs.autoComplete) {

                if (specs.initialSelection) {
                    specs.params.initSelection = function (element, callback) {
                        callback(specs.initialSelection);
                    }
                }

                specs.params.query = function (query) {
                    var data = {results: []};
                    var params = {"query": query.term};
                    if (typeof(specs.params.queryModifier) != "undefined") {
                        params = window[specs.params.queryModifier](params);
                    }

                    $.getJSON(specs.callbackURL, params, function (response) {
                        data.results = response.data;
                        query.callback(data);
                    });
                }
            }

            $("#" + specs.id).select2(specs.params);
        }
    });
})(jQuery);