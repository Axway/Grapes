function getParameterByName(name, url) {
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function isDisplayableByQueryParams(url) {
    var section = getParameterByName('section', url);

    return (null !== section && undefined !== section && '' !== section);
}

function displayByQueryParams(url, fns) {
    if(isDisplayableByQueryParams(url)) {
        var section = getParameterByName('section', url);

        var targets = fns.filter(f => section === f.section);

        if(targets.length !== 0) {
            let target = targets[0];

            var btn = $('#' + target.btn);

            if(undefined !== btn) {
                if(undefined !== target.args) {
                    let params = target.args.map(param => getParameterByName(param, url));
                    target.fn(...params);
                } else {
                    target.fn();
                }

                btn.addClass('active');

                resetURL(url);
            }
        }
        return true;
    } else {
        return false;
    }
}


function setFixedSelectOption(controlId, value) {
    $('#' + controlId)
        .find('option')
        .remove()
        .end()
        .append('<option value="' + value + '">' + value + '</option>')
        .val(value);
}

function navigateToModule(moduleName, version) {
    navigate('modules', [{name: 'moduleName', value: moduleName},
        {name: 'version', value: version}]);
}

function navigateToArtifact(groupId, artifactId, version) {
    navigate('artifacts', [{name: 'groupId', value: groupId},
        {name: 'artifactId', value: artifactId},
        {name: 'version', value: version}]);
}

function navigate(section, params) {
    let paramsPart = params.map(param => param.name + '=' +  encodeURIComponent(param.value));
    let location = '/webapp?section=' + section + '&' + paramsPart.join('&');
    window.location = location;
}

function resetURL(url) {
    if (!url) {
        url = window.location.href;
    }

    var stripped = url.substring(0, url.indexOf('?'));
    window.history.pushState('', '', stripped);
}


function splitToArtifactParts(value) {
    //  delivery.dependencies[index]
    return value.split(':');
}

function getOnClickHandler(parts) {
    if(parts.length === 0) {
        return 'navigateToArtifact("", "", "")';
    }

    var result = "navigateToArtifact(";
    result += '"';
    result += parts[0];
    result += '", "';
    result += parts[1] !== null ? parts[1] : "";
    result += '", "';
    result += parts[2] !== null ? parts[2] : "";
    result += '")';

    return result;
}