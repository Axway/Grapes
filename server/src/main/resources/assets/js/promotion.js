// Function specific to processing the report received from server in
// order to display them in in page

const sorted = ["critical", "major", "minor"];
const licenseRegexp = /(.*)(licensed as)(.*)/;

function tagsInReport(report) {
    return report.messages
        .map(function(msg) {return msg.tag.toLowerCase()})
        .filter(onlyUnique)
        .sort(function(a, b) {return sorted.indexOf(a) - sorted.indexOf(b)})
}

function onlyUnique(value, index, self) {
    return self.indexOf(value) === index;
}

function messagesByTag(report, tag) {
    return report.messages
        .filter(function(msg) {return msg.tag.toLowerCase() === tag.toLowerCase()})
        .map(function(msg) {return msg.body});
}

function capitalizeFirstLetter(tag) {
    var lowerCase = tag.toLowerCase();
    return lowerCase.charAt(0).toUpperCase() + lowerCase.slice(1);
}

function errorDescription(message) {
    return message.split(":")[0];
}

function getPieces(message) {
    var errDesc = errorDescription(message);
    if(errDesc.length === message.length) {
        return [];
    } else {
        var rest = message.substring(errDesc.length + 1);
        return rest.split(/,\s*(?![^()]*\))/gm);
    }
}

function makeArtifactLink(message) {
    var artifact = message.substring(0, message.lastIndexOf(":") + 4).trim();
    return makeLink("/artifact/" + encodeURIComponent(artifact), artifact);

}

function makeLicenseLink(licName) {
    return makeLink("/license/" + encodeURIComponent(licName), licName);
}

function makeLink(url, text) {
    return "<a href=\"" + url + "\">" + text + "</a>";
}

function asLinks(message) {

    if(licenseRegexp.test(message)) {
        var arr = licenseRegexp.exec(message);
        var artifact = arr[1].trim();
        var licName = arr[3].trim();
        return makeArtifactLink(artifact) + " licensed as " + makeLicenseLink(licName);
    } else {
        return makeArtifactLink(message);
    }
}

function cssClassByTag(tag) {
    return tag.toLowerCase();
}