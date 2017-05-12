// Executes reports against the server

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

function getPayload(name, version) {
    return {
        'reportId' : 1,
        'paramValues': {
           'name': name,
           'version': version
        }
    };
}
