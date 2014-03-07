Tapestry.Initializer.ProgressiveDisplay2 = function (specs) {

    window.setTimeout(function () {
        var element = $(specs.zoneId);
        element.getStorage().zoneId = specs.zoneId;
        var zm = Tapestry.findZoneManager(element);
        if (!zm) {
            return;
        }

        zm.updateFromURL(addQueryStringParameter(specs.url, 'zoneId', specs.zoneId));
    }, specs.period * 1000);

    function addQueryStringParameter(url, name, value) {
        if (url.indexOf('?') < 0) {
            url += '?'
        } else {
            url += '&';
        }
        value = escape(value);
        url += name + '=' + value;
        return url;
    }
};
