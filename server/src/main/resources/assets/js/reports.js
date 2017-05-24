// Executes reports against the server

function executeReportAsCSV(id, title) {
    var payloadStr = document.getElementById(id).innerText;

    $.ajax({
        type: "POST",
        url: "/reports/execution",
        data: payloadStr,
        contentType: "application/json",
        headers: {
            'Accept' : 'text/csv'
        },
        success: function (data) {
            var blob = new Blob([data], {type: "text/csv;charset=utf-8"});
            saveAs(blob, title);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.error('Error executing report ' + thrownError);
            console.error(xhr.responseText);
        }
    });
}

function runComparisonReport(n1, v1, n2, v2, resultsCb) {
    $.ajax({
        type: "POST",
        url: "/reports/execution",
        data: JSON.stringify(getReportRequestPayload(2, ['name1=' + n1, 'version1=' + v1, 'name2=' + n2, 'version2=' + v2])),
        contentType: "application/json",
        headers: {
            'Accept' : 'application/json'
        },
        success: function (data) {
            resultsCb(data);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            resultsCb(new Error(thrownError.message + ' ' + xhr.responseText));
        }
    });
}




function commercialDeliveriesReport(product, commercialName, commercialVersion) {
    $.ajax({
        type: "POST",
        url: "/reports/execution",
        data: JSON.stringify(getPayload(commercialName, commercialVersion)),
        contentType: "application/json",
        headers: {
            'Accept' : 'text/csv'
        },
        success: function (data) {
            var blob = new Blob([data], {type: "text/csv;charset=utf-8"});
            saveAs(blob, commercialName + " " + commercialVersion + " - Third Party Licenses.csv");
        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.error('Error executing report ' + thrownError);
        }
    });
}

function getReportRequestPayload(id, paramNameValues) {
    var result = {
        'reportId' : id,
        'paramValues' : {}
    };

    paramNameValues.forEach(function (pair) {
        result.paramValues[getIndex(pair, '=', 0)] = getIndex(pair, '=', 1);
    });

    return result;
}

function getPayload(name, version) {
    return {
        'reportId' : 1,
        'paramValues': {
           'name': name,
           'version': version
        }
    };
}

// Product Name / 1.2.4 -> Product Name
function getCommercialName(entry) {
    return getIndex(entry, '/', 0);
}

function getCommercialVersion(entry) {
    return getIndex(entry, '/', 1);
}

function getIndex(entry, sep, pos) {
    var parts = entry.split(sep);
    if(parts.length > pos) {
        return parts[pos];
    } else {
        return entry;
    }
}

function reportJSONToUI(desc, response) {
    var html = "<a href='#' onclick='executeReportAsCSV(\"request\",\"" + desc + ".csv\")'>Export Report as CSV</a>";

    // Store it for later being able to execute the request once again.
    html += "<div id='request' style='display:none'>";
    html += JSON.stringify(response.request);
    html += "</div>";


    html += '<h3>' + desc + '</h3>';
    html += '<h4>Report Parameters</h4>';
    html += "<ul>";
    var params = response.request.paramValues;
    for(param in params) {
        html += "<li>";
       html += param;
       html += ": ";
       html += params[param];
       html += "</li>";
    }
    html += "</ul>";

    html += '<h4>Report Data</h4>';
    html += '<table class="table table-bordered table-hover sortable" id="results">';
    html += '<thead>\n';

    response.resultColumnNames.forEach(function(colName) {
        html += '<th class="header">';
        html += '<span>';
        html += colName;
        html += '</span>';
        html += '</th>\n';
    });

    html += '</thead>\n';
    html += '<tbody>\n';

    response.data.forEach(function(row) {
        html += '<tr class="license">';
        row.forEach(function(cell) {
           html += '<td>';
           html += cell;
           html += '</td>';
        });
        html += '</tr>';
    });

    html += '</tbody>\n';
    html += '</table>';

    return html;
}
