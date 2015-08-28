//todo wouldn't it be nice if each tab was contained in it own class
//todo color code the artifact red if do not use flag is set or green if not set as well as the field do not use.

/****************************************Button Handlers************************************************/
var GrapesArtifactHandlers = {

    saveArtifact: function () {

        var gavc = $(document.body).data("gavc");
        var oldprovider = $("#" + GrapesCommons.jq2(gavc)).find('td.provider').text();
        var oldurl = $("#" + GrapesCommons.jq2(gavc)).find('td.downloadUrl').text();
        var olddonotuse = $("#" + GrapesCommons.jq2(gavc)).find('td.doNotUse').text();
        var artprovider = $('#editArtifact').find('input[id="idProvider"]').val();
        var artUrl = $('#editArtifact').find('input[id="idUlr"]').val();
        var artDoNotUse = $('#editArtifact').find('input[id="idDoNotUse"]').prop("checked");

        if (artprovider != oldprovider) {
            console.log("chaning provicer");
            GrapesCommons.postRestResource(GrapesArtifactUrls.updateArtifactProvider(gavc, artprovider), '', GrapesArtifact.reloadPage);
        }
        if (artUrl != oldurl) {
            console.log("chaning dl url");
            GrapesCommons.postRestResource(GrapesArtifactUrls.updateArtifactUrl(gavc, artUrl), '', GrapesArtifact.reloadPage);
        }
        if (artDoNotUse != olddonotuse) {
            console.log("chaning donot use");
            GrapesCommons.postRestResource(GrapesArtifactUrls.updateArtifactDoNotUse(gavc, artDoNotUse), '', GrapesArtifact.reloadPage);
        }

        // GrapesCommons.getRestResources(ArtifactUrls.listNamesUl, setArtifactGroupIdsList);
        $("#artifactUpdateModal").modal('hide');
        //  $('#editArt').show();
    },

    updateArtifact: function () {
        GrapesArtifactHandlers.createArtifactUpdateModal();
        $('#editArt').hide();
        $('#artifactUpdateModal').show();

    },

    createArtifactUpdateModal: function () {
        var gavc = $(document.body).data("gavc");
        var oldprovider = $("#" + GrapesCommons.jq2(gavc)).find('td.provider').text();
        var oldurl = $("#" + GrapesCommons.jq2(gavc)).find('td.downloadUrl').text();
        var olddonotuse = $("#" + GrapesCommons.jq2(gavc)).find('td.doNotUse').text();


        $('#editArtifact').find('input[id="idProvider"]').val(oldprovider).attr("placeholder", oldprovider);
        $('#editArtifact').find('input[id="idUlr"]').val(oldurl).attr("placeholder", oldurl);

        if (olddonotuse === true) {
            $('#editArtifact').find('input[id="idDoNotUse"]').val(olddonotuse).attr("checked", true);
        }
        else {
            $('#editArtifact').find('input[id="idDoNotUse"]').val(olddonotuse);
        }
        return;
    }
}

/*******************************Artifact URLS***************************************/
var GrapesArtifactUrls = {
    root: "/artifact",
    listGavcs: "/artifact/gavcs",
    artifactGroupIds: "/artifact/groupIds",
    artifact: function (gavc) {
        return this.root + "/" + encodeURIComponent(gavc);
    },
    updateArtifactUrl: function (gavc, url) {

        return this.root + "/" + encodeURIComponent(gavc) + "/downloadurl?url=" + encodeURIComponent(url);
    },
    updateArtifactDoNotUse: function (gavc, doNotUse) {

        return this.root + "/" + encodeURIComponent(gavc) + "/donotuse?doNotUse=" + encodeURIComponent(doNotUse);
    },
    updateArtifactProvider: function (gavc, provider) {

        return this.root + "/" + encodeURIComponent(gavc) + "/provider?provider=" + encodeURIComponent(provider);
    },
    artAncestors: function (gavc) {
        console.log("inside the create path for anscestors: " + this.root + "/" + encodeURIComponent(gavc) + "/ancestors");
        return this.root + "/" + encodeURIComponent(gavc) + "/ancestors";
    },

    artifactIds: function (groupId) {
        return this.root + "/all?groupId=" + encodeURIComponent(groupId);
    },
    artifactVersions: function (groupId, artfactId) {
        return this.root + "/all?groupId=" + encodeURIComponent(groupId)
            + "&artifactId=" + encodeURIComponent(artfactId);
    },
    artifactGav: function (groupId, artifactId, version) {
        return this.root + "/all?groupIds=" + encodeURIComponent(groupId) +
            "&artifactId=" + encodeURIComponent(artifactId) +
            "&version=" + encodeURIComponent(version);
    }
}

/*******************************Artifact Views***************************************/
var GrapesArtifactViews = {
    setArtifactGroupIdsList: function (jsonData) {

        var option = '<option value="false" >Choose a Group ID</option>';
        for (var i = 0; i < jsonData.length; i++) {
            option += '<option class="grapesOptionSelect value="' + jsonData[i] + '">' + jsonData[i] + '</option>';
        }
        $('#artifactGroupIdsList').empty().append(option);

        return;
    },
    setArtifactVersionsList: function (jsonData) {
        var list = [];
        var option = '<option value="false" >Choose a Version</option>';
        for (var i = 0; i < jsonData.length; i++) {
            list.push(jsonData[i].version);

        }
        $.unique(list);
        for (i = 0; i < list.length; i++) {
            option += '<option class="grapesOptionSelect value="' + list[i] + '">' + list[i] + '</option>';
        }
        $('#artifactVersionsList').empty().append(option);
        $('#artifactVersionsList').show();
        $('#artifactVersionsListLabel').show();

        return;
    },
    setArtifactIdsList: function (jsonData) {
        var list = [];
        var option = '<option value="false" >Choose an Artifact ID</option>';
        for (var i = 0; i < jsonData.length; i++) {
            list.push(jsonData[i].artifactId);

        }
        $.unique(list);
        for (i = 0; i < list.length; i++) {
            option += '<option class="grapesOptionSelect value="' + list[i] + '">' + list[i] + '</option>';
        }
        $('#artifactIdsList').empty().append(option);
        $('#artifactIdsList').show();
        $('#artifactIdsListLabel').show();

        return;
    },
    showArtifactAdminElements: function () {
        GrapesCommons.setIsAdmin();
        if (GrapesCommons.getIsAdmin()) {
            console.log("show damnit");
            $("#editArt").show();
        }

    },
    createArtifactViews: function (jsonData) {

        GrapesArtifactOverviewTab.createTab(jsonData);
        GrapesArtifactAncestorTab.createTab(jsonData);
        return;
    }

}
/*******************************Artifact Tabs************************************/
var GrapesArtifactOverviewTab = {

    createTab: function (json) {
        $("#artAdminTable").empty();
        $("#artOverviewTable").empty();
        var tableTitle = "Artifact Overview";
        var table = $('<table/>', {
            class: "table table-striped",
            id: "adminArtTable"
        });

        table.append("<thead><tr><td>" + tableTitle + "</td></tr></thead>");
        $("#artId").text(json.name);
        console.log("inside the admin tab");
        console.log("we are here 1" + json);

        $.each(json, function (key, val) {

            var table2 = $('<table/>', {
                class: "table table-striped",
                id: GrapesCommons.jq2(val._id),
                style: "display : none"

            });

            var nameCol = $("<td>").text("GAVC");
            var namevalCol = $("<td>").append($('<a>').text(val._id).click(function () {
                $("#artAdminTable").children('table').hide();
                $(document.body).data("gavc", val._id);
                $("#artId").text("Artifact:  " + val._id);
                $("#" + GrapesCommons.jq2(val._id)).show();
            }));
            var firstrow = $("<tr/>").append(nameCol).append(namevalCol);
            table.append(firstrow);

            console.log(val);

            $.each(val, function (key, value) {

                console.log(key, value);
                if (key !== "_id" && key != "dataModelVersion") {
                    var col1 = $("<td>").text(key);
                    var col2 = $("<td>").text(value).addClass(key);
                    var row = $("<tr/>").append(col1).append(col2);
                    table2.append(row);
                }

            });
            $("#artAdminTable").append(table2);
        });

        $("#artOverviewTable").empty().append(table);
        $("#artInfo").hide();
        GrapesArtifactViews.showArtifactAdminElements();
        //todo create table of the license or else state that there are no licenses
    }

}

var GrapesArtifactAncestorTab = {

    createTab: function (json) {
        var gavc = json._id;

        console.log("creating ancestors tab for " + gavc);
        GrapesCommons.getRestResources(GrapesArtifactUrls.artAncestors(gavc), this.processAncestors);
    },

    processAncestors: function (jsonData) {
        //todo this should have links that are clickable that take you to the module page
        console.log("I should have a list of anscrots: " + jsonData.length);
        if (jsonData.length > 0) {
            var tableGavc = $('<table/>', {
                class: "table table-striped",
                id: "gavcTable"
            });
            var table = $('<table/>', {
                class: "table table-striped",
                id: "ancestorTable"
            });
            var nameGavc = $("<td>").text("GAVC");
            var gavcValCol = $("<td>").append($('<a>').text(val._id).click(function () {
                $("#ancestorsList").children('table').hide();
                $(document.body).data("gavc", val._id);

                $("#" + GrapesCommons.jq2(val._id)).show();
            }));
            var firstrow = $("<tr/>").append(nameCol).append(namevalCol);
            table.append(firstrow);

            var col1 = $("<td>").text("");
            var col2 = $("<td>").text("Ancestor Name");
            var col3 = $("<td>").text("version");
            var row = $("<tr/>").append(col1).append(col2).append(col3);
            table.append(row);
            $.each(jsonData, function (key, value) {
                col1 = $("<td>").text(key);
                col2 = $("<td>").text(value.name);
                col3 = $("<td>").text(value.version);
                row = $("<tr/>").append(col1).append(col2).append(col3);
                console.log(key + " " + value);
                console.log("ancestor is " + value.name + value + value.version);
                table.append(row);
            });
        }
        else {
            $("#ancestorInfoMsg").text("There are no ancestors for this artifact");
        }
        //create tab view here
        $("#ancestorsList").empty().append(table);
    }
}

/****************************************INIT Functions**************************************************/
var GrapesArtifact = {

    initArtifactPage: function () {
        var selectedGroupId = "";
        //set list of organization names
        GrapesCommons.getRestResources(GrapesArtifactUrls.artifactGroupIds, GrapesArtifactViews.setArtifactGroupIdsList);

        //list on click function
        $('#artifactGroupIdsList').click(function () {
            $("#artifactIdsList").hide();
            $("#artifactVersionsList").hide();
            $("#artifactIdsListLabel").hide();
            $("#artifactVersionsListLabel").hide();
            selectedGroupId = $("#artifactGroupIdsList option:selected").val();
            $(document.body).data("artifactGroupId", selectedGroupId);
            //retrieve information on the organization selected from the list.

            if(selectedGroupId !=="false"){
                GrapesCommons.getRestResources(GrapesArtifactUrls.artifactIds(selectedGroupId), GrapesArtifactViews.setArtifactIdsList);
            }
           });

        //list on click function
        $('#artifactIdsList').click(function () {

            $("#artifactVersionsList").hide();
            $("#artifactVersionsListLabel").hide();
            var selectedArt = $("#artifactIdsList option:selected").val();
            $(document.body).data("artifactId", selectedArt);
            console.log("the path we look for is: " + GrapesArtifactUrls.artifactVersions($(document.body).data("artifactGroupId"), selectedArt));
            //retrieve information on the organization selected from the list.
                if(selectedArt !=="false") {
                    GrapesCommons.getRestResources(GrapesArtifactUrls.artifactVersions($(document.body).data("artifactGroupId"), selectedArt), GrapesArtifactViews.setArtifactVersionsList);
                }
        });

        //list on click function
        $('#artifactVersionsList').click(function () {
            var selectedArtVersion = $("#artifactVersionsList option:selected").val();
            $(document.body).data("artifactVersion", selectedArtVersion);
            console.log("end path is : " + GrapesArtifactUrls.artifactGav($(document.body).data("artifactGroupId"),
                $(document.body).data("artifactId"), selectedArtVersion));
            //retrieve information on the organization selected from the list.
                if(selectedArtVersion !=="false") {
                    GrapesCommons.getRestResources(GrapesArtifactUrls.artifactGav($(document.body).data("artifactGroupId"),
                        $(document.body).data("artifactId"), selectedArtVersion), GrapesArtifactViews.createArtifactViews);
                }
        });
        //initialize button handlers
        $('#updateArtifactSaveBtn').click(GrapesArtifactHandlers.saveArtifact);
        $('#editArt').click(GrapesArtifactHandlers.updateArtifact);

        return;
    },

    reloadPage: function () {
        var groupId = $(document.body).data("artifactGroupId");
        var artifactId = $(document.body).data("artifactId");
        var version = $(document.body).data("artifactVersion");
      //  alert("reloading the page" + GrapesArtifactUrls.artifactGav(groupId, artifactId, version));
        GrapesCommons.getRestResources(GrapesArtifactUrls.artifactGav(groupId, artifactId, version), GrapesArtifactViews.createArtifactViews);
        //  GrapesCommons.getRestResources(GrapesArtifactUrls.artifact($(document.body).data("gavc")), createArtifactViews);
    }
}


$(document).ready(function () {

    GrapesArtifact.initArtifactPage();
});