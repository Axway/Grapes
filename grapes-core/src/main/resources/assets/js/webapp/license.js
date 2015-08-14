//todo should make a section for button handlers
//todo look at restructing
/*******************************COMMONS FUNCTIONS***************************************/
var GrapesLicense = {
	
reloadPage: function () {
    GrapesCommons.getRestResources(LicenseUrls.license( $(document.body).data("licId")), GrapesLicenseView.createViews);
},
init: function () {
console.log("Init Lic");
    var selectedLic = "";
    //set list of organization names
    GrapesCommons.getRestResources(LicenseUrls.listNames, GrapesLicenseView.setLicenseList);
    $('#LicenseList').click(function () {
        selectedLic = $("#LicenseList option:selected").text();



        //retrieve information on the organization selected from the list.
        GrapesCommons.getRestResources(LicenseUrls.license(selectedLic), GrapesLicenseView.createViews);


    });
    console.log($("#LicenseList option:selected").text());


    $('#createLicSaveBtn').click(GrapesLicenseHandler.createLicense);
       $('#delLicProceedBtn').click(GrapesLicenseHandler.removeLicense);
     $('#editLic').click(GrapesLicenseHandler.editLicenseFields);
    $('#saveLic').click(GrapesLicenseHandler.saveLicenseField);


    return;
}


}

var GrapesLicenseHandler = { 

createLicense: function () {
    //todo check handler because unknow should default to true?
    console.log("save stuff");
    var licName = $('#createNewLicense').find('input[id="licName"]').val();
    var licLongName = $('#createNewLicense').find('input[id="licLongName"]').val();
    var licUrl = $('#createNewLicense').find('input[id="licURL"]').val();
    var licComments = $('#createNewLicense').find('input[id="licComments"]').val();
    var licRegexp = $('#createNewLicense').find('input[id="licRegexp"]').val();

    console.log("I am empty?", licName, licLongName, licUrl, licComments, licRegexp);

    if (!licName || licName.length === 0
        || !licLongName || licLongName.length === 0) {
        alert("license name, longname are required fields");
    }
    else {
        var data = '{ "name": "' + licName + '", "longName": "' + licLongName + '", "comments": "' + licComments
            + '", "regexp": "' + licRegexp + '", "url": "' + licUrl + '", "approved": false }';



        GrapesCommons.postRestResource(LicenseUrls.root, data,GrapesLicense.reloadPage);
        GrapesCommons.getRestResources(LicenseUrls.listNames, GrapesLicenseView.setLicenseList);


        $("#licModal").modal('hide');
    }
    return;
},
removeLicense:function () {
    //todo add a response notifaction to the html page
    console.log("DELETE ME muahahahah");

    GrapesCommons.deleteRestResource(LicenseUrls.license($("#deleteLicBtn").data("licName")), GrapesCommons.load);
    $('#deleteLicModal').modal('hide');
},

editLicenseFields:function () {
    alert("I edit stuff someday");
    $('#editLic').hide();
    $('#saveLic').show();
    $('.hideme').toggle();
    $('.hidetext').toggle();
},

saveLicenseField: function () {
    alert("i saved suff");
    var licId = $("#deleteLicBtn").data("licName");

    var licUnknownField = $('#adminLicTable').find('input[name="unknown"]:checked').val();
    var licApproved = $('#adminLicTable').find('input[name="approved"]:checked').val();
    var licLongName = $('#adminLicTable').find('input[name="longName"]').val();
    var licUrl = $('#adminLicTable').find('input[name="url"]').val();
    var licComments = $('#adminLicTable').find('input[name="comments"]').val();
    var licRegexp = $('#adminLicTable').find('input[name="regexp"]').val();
    var data = '{ "name": "' + licId + '", "longName": "' + licLongName + '", "comments": "' + licComments
        + '", "regexp": "' + licRegexp + '", "url": "' + licUrl + '", "approved": '+licApproved+' , "unknown" : '+licUnknownField+'}';

console.log(data);
    alert("is data ok?");

    GrapesCommons.postRestResource(LicenseUrls.root, data,GrapesLicense.reloadPage);
    GrapesCommons.getRestResources(LicenseUrls.listNames, GrapesLicenseView.setLicenseList);

    //$('#createlicName').find('input[name="licName"]').val('');
    //$('#createlicName').find('input[name="corpidlist"]').val('');
    $("#licModal").modal('hide');


    $('#editLic').show();
    $('#saveLic').hide();

    $('.hideme').toggle();
    $('.hidetext').toggle();

}
}


var GrapesLicenseView = {
	showAdminElements: function (){
    GrapesCommons.setIsAdmin();
    if(GrapesCommons.getIsAdmin()) {
        console.log("show damnit");
        $("#deleteLicBtn").show();
        $("#editLic").show()
    }
},
	setLicenseList: function (jsonData) {

    var option = '';
    for (var i = 0; i < jsonData.length; i++) {
        option += '<option value="' + jsonData[i] + '">' + jsonData[i] + '</option>';
    }
    $('#LicenseList').empty().append(option);

  
},
	createViews: function (jsonData) {

    GrapesLicenseTabOverview.createTab(jsonData);
    return;
}


}
var GrapesLicenseTabOverview = {
	createTab: function (json) {
    $(document.body).data("licId", json._id);
    var tabletitle = "License Information";
    var table = $("<table/>").addClass(' table table-striped');
    $("#licId").text(json.name); var tabletitle = "License Information";
    var table = $('<table/>', {
        class:"table table-striped",
        id: "adminLicTable"
    });

    table.append("<thead><tr><td>" + tabletitle + "</td></tr></thead>");

    var nameCol = $("<td>").text("name");
    var namevalCol = $("<td>").text(json.name);
    var firstrow = $("<tr/>").append(nameCol).append(namevalCol);
    table.append(firstrow);

    $.each(json, function (key, val) {

        var input = $('<input />', {
            type: "text",
            name: key,
            value: val,
            style: "display : none",
            class: "hideme"
        });

        switch (key) {
            case "_id": //do nothing we don't display
            case "name":
            case "dataModelVersion":
                break;
            case "longName":
            case "url":
            case "comments":
            case "regexp":
                var col1 = $("<td>").text(key);
                var col2 = $("<td>").addClass("hideme").text(val);
                var col3 = $("<td>").addClass("hideme").attr("style", "display:none").append(input);
                var row = $("<tr/>").append(col1).append(col2).append(col3);
                table.append(row);

                break;
            case  "approved":
            case "unknown":

                var yes = $("<label for='rtrue'> Yes </label>");
                var no = $("<label for='rfalse'> no </label>");
                var radioTrue = $('<input />', {
                    type: "radio",
                    name: key,
                    value: true,
                    id: "rtrue"
                });

                var radioFalse = $('<input />', {
                    type: "radio",
                    name: key,
                    value: false,
                    id: "rfalse"
                });



                var col1 = $("<td>").text(key);
                var col2 = $("<td>").text(val).addClass("hideme");
                var col3 = $("<td>").addClass("hideme").attr("style", "display:none");
                if (val === true) {
                    radioTrue.attr('checked', true);
                }
                else {
                    radioFalse.attr('checked', true);
                }
                col3.append(radioTrue).append(yes);
                col3.append(radioFalse).append(no);
                var row = $("<tr/>").append(col1).append(col2).append(col3);
                table.append(row);
                break;

        }
    });
    var nameCol = $("<td>").text("dataModelVersion");
    var namevalCol = $("<td>").text(json.dataModelVersion);
    var firstrow = $("<tr/>").append(nameCol).append(namevalCol);
    table.append(firstrow);

    $("#licAdminTable").empty().append(table);
    $("#deleteLicBtn").text("Delete License: " + json.name)
        .data("licName", json.name)
        .show();

    $("#licInfo").hide();
    GrapesLicenseView.showAdminElements();

}

}
var GrapesLicenseTabArtifact = {
	creatTab: function(){//todo
		}
}
var GrapesLicenseTabModule = {
	creatTab: function(){//todo
		}
}
var GrapesLicenseTabProduct = {
	creatTab: function(){//todo
		}
}

var LicenseUrls = {
    root: "/license",
    listNames: "/license/names",
    license: function (licName) {
        return "/license/" + encodeURIComponent(licName);
    },
    licenseApproval: function (licName, boolAprov) {//POST <dm_url>/license/<name>?approved=<boolean>
    }
}


$(document).ready(function () {
    //maybe we call this with a body onload for the page?
    GrapesLicense.init();
});