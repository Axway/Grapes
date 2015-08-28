
var GrapesOrganization = {
    initPage: function () {
        var selectedOrg = "";
        //set list of organization names
        GrapesCommons.setIsAdmin();
        if (GrapesCommons.getIsAdmin()) {
        console.log("Are were here?");
            $("#createOrgBtn").show();
        }
        else{
            console.log("Are were wtf?");
            $("#createOrgBtn").hide();
        }
        GrapesCommons.getRestResources(GrapesOrganizationUrls.listNames, GrapesOrganizationViews.setOrganizationList);
        $('#organizationList').click(function () {
            selectedOrg = $("#organizationList option:selected").text();

            //retrieve information on the organization selected from the list.
            GrapesCommons.getRestResources(GrapesOrganizationUrls.organization(selectedOrg), GrapesOrganizationViews.createOrganizationViews);


        });



        $('#createOrgSaveBtn').click(GrapesOrganizationHandlers.createOrganization);

        $('#updateOrgSaveBtn').click(GrapesOrganizationHandlers.updateCorporateId);


        $('#delOrgProceedBtn').click(GrapesOrganizationHandlers.removeOrganization);
        $('#addOrgCorpIdBtn').click(GrapesOrganizationHandlers.addCorporateId);
    },
    reloadOrgPage: function () {
        GrapesCommons.getRestResources(GrapesOrganizationUrls.organization($("#deleteOrgBtn").data("orgName")), GrapesOrganizationViews.createOrganizationViews);
    }
}
var GrapesOrganizationViews = {
    setOrganizationList: function (jsonData) {

        var option = '';
        for (var i = 0; i < jsonData.length; i++) {
            option += '<option value="' + jsonData[i] + '">' + jsonData[i] + '</option>';
        }
        $('#organizationList').empty().append(option);


    },
    createOrganizationViews: function (jsonData) {
        // createAdminTab(jsonData);
        GrapesOrganizationTabOverview.createTab(jsonData);

    },
    showOrgAdminElements: function () {
        console.log("inside show elements");
        console.log("admin is: " + GrapesCommons.getIsAdmin());
        GrapesCommons.setIsAdmin();
        if (GrapesCommons.getIsAdmin()) {
            console.log("show damnit");
            $("#deleteOrgBtn").show();
            $("#addCorpIdform").show();
            $(".isAdminhide").show();
            $("#createOrgBtn").show();
        }
        else{
            $("#deleteOrgBtn").hide();
            $("#addCorpIdform").hide();
            $(".isAdminhide").hide();
            $("#createOrgBtn").hide();
        }
    },
    getActionBarForExtensionOrgs:function (orgName, corpId) {
        var bar = $("<div></div>").addClass("bundle-action-bar pull-right").addClass("btn-toolbar").attr("role",
            "toolbar");
        var inner = $("<div></div>").addClass("btn-group");
        var uninstall = $("<button type=\"button\" class=\"btn btn-danger btn-xs\"><span class=\"glyphicon glyphicon-remove\"></span></button>");
        uninstall.click(function () {
            GrapesCommons.deleteRestResource(GrapesOrganizationUrls.getCorpIdUrl(orgName, corpId), GrapesOrganization.reloadOrgPage, orgName);

        });
        var extupdate = $("<button type=\"button\" class=\"btn btn-primary btn-xs\" data-toggle=\"modal\" data-target=\"#corpIdModal\"><span class=\"glyphicon glyphicon-pencil\"></span></button>");
        extupdate.click(function () {
            GrapesOrganizationHandlers.updateCorpId(corpId, orgName)
        });
        inner.append(uninstall);
        inner.append(extupdate);
        bar.append(inner);
        return $("<td></td>").addClass("isAdminhide").append(bar).hide();
    }
}
var GrapesOrganizationTabOverview = {
    createTab:function (json) {
        var tabletitle = "Corperate Id Prefixes";
        var orgId = document.getElementById("orgId");
        var table = $("<table/>").addClass(' table table-striped');
        $("#orgId").text(json.name);
        var title = $("<thead><tr><td>" + tabletitle + "</td> </tr></thead>");
        title.addClass("grapesTableHeader");
        table.append(title);

        $.each(json.corporateGroupIdPrefixes, function (key, val) {
            var row = $("<tr/>").append("<td/>").text(val).append(GrapesOrganizationViews.getActionBarForExtensionOrgs(json.name, val));
            table.append(row);

            console.log("key", key, " value ", val);

        });
        $("#corpIdTable").empty().append(table);
        $("#orgInfo").hide();
        $("#deleteOrgBtn").text("Delete Organization: " + json.name)
            .data("orgName", json.name);
        GrapesOrganizationViews.showOrgAdminElements();

    }
}
var GrapesOrganizationHandlers = {
    createOrganization: function () {
        console.log("save stuff for orgs");
        var orgName = $('#createOrgName').find('input[name="orgname"]').val();
        var listofCorpIds = $('#createOrgName').find('input[name="corpidlist"]').val();

        if (!orgName || orgName.length === 0) {
            //alert("needs to be not empty or lcik close to cancel creation");
        }
        else {
            var data = '{ "name": "' + orgName + '", "corporateGroupIdPrefixes": [' + GrapesCommons.stringtoarray(listofCorpIds) + ']}';
            //should refresh list as the call back.
            GrapesCommons.postRestResource(GrapesOrganizationUrls.root, data);
            GrapesCommons.getRestResources(GrapesOrganizationUrls.listNames, GrapesOrganizationViews.setOrganizationList);
            GrapesOrganization.reloadOrgPage("do", orgName);
            $('#createOrgName').find('input[name="orgname"]').val('');
            $('#createOrgName').find('input[name="corpidlist"]').val('');
            $("#orgModal").modal('hide');
        }

    },

    updateCorporateId: function () {
        //todo should have a warning the changing and editing ids does not affect names in the artifcats or modules.
        console.log("save stuff 2");
        var oldcorpId = $('#oldCorpId').data("corpId");
        var orgName = $('#oldCorpId').data("orgName");
        var newcorpId = $('#editCorpId').find('input[name="corpId"]').val();

        if (!newcorpId || newcorpId.length === 0) {
            //alert("needs to be not empty or click close to cancel creation");
        }
        else {
            GrapesCommons.postRestResource(GrapesOrganizationUrls.addCorpIdUrl(orgName), '"' + newcorpId + '"', GrapesOrganizationHandlers.postcallback);
            //potentially the remove could happen before the add it is async so the reshrsh could not show the new id
            GrapesCommons.deleteRestResource(GrapesOrganizationUrls.getCorpIdUrl(orgName, oldcorpId), GrapesOrganization.reloadOrgPage, orgName);
            $('#editCorpId').find('input[name="corpId"]').val('');
            $("#corpIdModal").modal('hide');
        }

    },

    addCorporateId: function () {
        console.log("stupid button did you even press?");
        var listofCorpIds = $('#addCorpIdform').find('input[name="addCorpId"]').val();
        if (listofCorpIds || listofCorpIds.length > 0) {
            GrapesCommons.postRestResource(GrapesOrganizationUrls.addCorpIdUrl($("#deleteOrgBtn").data("orgName")), GrapesCommons.stringtoarray(listofCorpIds), GrapesOrganization.reloadOrgPage);
            $('#addCorpIdform').find('input[name="addCorpId"]').val('');


            $('#addCorpIdform').find('input[name="addCorpId"]').val('');

        }


    },
    postcallback: function () {
        console.log("i do nothing");
    },
    removeOrganization: function () {
        console.log("DELETE ME muahahahah");
        GrapesCommons.deleteRestResource(GrapesOrganizationUrls.organization($("#deleteOrgBtn").data("orgName")), GrapesCommons.load);
        $('#deleteOrgModal').modal('hide');
    },

    updateCorpId:function (corpId, orgName) {
    $("#oldCorpId").data("orgName", orgName);
    $("#oldCorpId").data("corpId", corpId).text(corpId);

}

}
var GrapesOrganizationUrls = {
    root: "/organization",
    listNames: "/organization/names",
    organization: function (orgName) {
        return "/organization/" + encodeURIComponent(orgName);
    },
    addCorpIdUrl: function (orgName) {
        return "/organization/" + encodeURIComponent(orgName) + "/corporateGroupIds";
    },
    getCorpIdUrl: function (orgName, corpId) {
        return "/organization/" + encodeURIComponent(orgName) + "/corporateGroupIds/" + encodeURIComponent(corpId);
    }
}

$(document).ready(function () {

    GrapesOrganization.initPage();
});