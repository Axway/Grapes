//todo how come my delete doesnt pass the data?
//todo should make a section for button handlers
//todo look at restructing
var GrapesOrganization = {}
var GrapesOrganizationHandlers = {}
var GrapesOrganizationViews = {}
var GrapesOrganizationTabOverview = {}
var GrapesOrganizationTab = {}

var OrgUrls = {
    orgRoot: "/organization",
    listNamesUrl: "/organization/names",
    getCorpIdUrl: function (orgName, corpId) {
        return "/organization/" + encodeURIComponent(orgName) + "/corporateGroupIds/" + encodeURIComponent(corpId);
    },
    addCorpIdUrl: function (orgName) {
        return "/organization/" + encodeURIComponent(orgName) + "/corporateGroupIds";
    },
    orgUrl: function (orgName) {
        return "/organization/" + encodeURIComponent(orgName);
    }
}

function stringtoarray(string) {
    //todo alphabetise
    var arrays = string.replace(/ /g, '').split(",");

    return '"' + arrays.join('","') + '"';


}


function load() {
    console.log("need to reload page");
    location.reload();
}
/***************************************************************************************/
function Organization(orgName, corpIdList) {
    this.orgName = orgName;
    this.corpIdList = corpIdList;
}

function setOrganizationList(jsonData) {

    var option = '';
    for (var i = 0; i < jsonData.length; i++) {
        option += '<option value="' + jsonData[i] + '">' + jsonData[i] + '</option>';
    }
    $('#organizationList').empty().append(option);

    return;
}

/*********functions used in ajax callbacks**************************/
function createOrganizationViews(jsonData) {
   // createAdminTab(jsonData);
    createOverviewTab(jsonData);
    return;
}

function reloadOrgPage() {
    GrapesCommons.getRestResources(OrgUrls.orgUrl($("#deleteOrgBtn").data("orgName")), createOrganizationViews);
}

/*******************************organization Tabs************************************/
function showOrgAdminElements(){
    console.log("inside show elements");
    console.log("admin is: "+GrapesCommons.getIsAdmin());
    GrapesCommons.setIsAdmin();
    if(GrapesCommons.getIsAdmin()){
        console.log("show damnit");
        $("#deleteOrgBtn").show();
        $("#addCorpIdform").show();
        $(".isAdminhide").show();
    }
    }

function createOverviewTab(json) {
    var tabletitle = "Corperate Id Prefixes";
    var orgId = document.getElementById("orgId");
    var table = $("<table/>").addClass(' table table-striped');
    $("#orgId").text(json.name);
    table.append("<thead><tr><td>" + tabletitle + "</td> </tr></thead>");

    $.each(json.corporateGroupIdPrefixes, function (key, val) {
        var row = $("<tr/>").append("<td/>").text(val).append(getActionBarForExtensionOrgs(json.name, val));
        table.append(row);

        console.log("key", key, " value ", val);

    });
    $("#corpIdTable").empty().append(table);
    $("#orgInfo").hide();
    $("#deleteOrgBtn").text("Delete Organization: " + json.name)
        .data("orgName", json.name);
    showOrgAdminElements();

}

/****************************************MISC Functions******************************************/



function getActionBarForExtensionOrgs(orgName, corpId) {
    var bar = $("<div></div>").addClass("bundle-action-bar pull-right").addClass("btn-toolbar").attr("role",
        "toolbar");
    var inner = $("<div></div>").addClass("btn-group");
    var uninstall = $("<button type=\"button\" class=\"btn btn-default btn-xs\"><span class=\"glyphicon glyphicon-remove\"></span></button>");
    uninstall.click(function () {
        GrapesCommons.deleteRestResource(OrgUrls.getCorpIdUrl(orgName, corpId), reloadOrgPage, orgName);

    });
    var extupdate = $("<button type=\"button\" class=\"btn btn-default btn-xs\" data-toggle=\"modal\" data-target=\"#corpIdModal\"><span class=\"glyphicon glyphicon-repeat\"></span></button>");
    extupdate.click(function () {
        updateCorpId(corpId, orgName)
    });
    inner.append(uninstall);
    inner.append(extupdate);
    bar.append(inner);
    return $("<td></td>").addClass("isAdminhide").append(bar).hide();
}


function updateCorpId(corpId, orgName) {
    $("#oldCorpId").data("orgName", orgName);
    $("#oldCorpId").data("corpId", corpId).text(corpId);

}

/****************************************Button Handlers************************************************/

function createOrganization() {
    console.log("save stuff for orgs");
    var orgName = $('#createOrgName').find('input[name="orgname"]').val();
    var listofCorpIds = $('#createOrgName').find('input[name="corpidlist"]').val();

    if (!orgName || orgName.length === 0) {
        //alert("needs to be not empty or lcik close to cancel creation");
    }
    else {
        var data = '{ "name": "' + orgName + '", "corporateGroupIdPrefixes": [' + stringtoarray(listofCorpIds) + ']}';
        //should refresh list as the call back.
        GrapesCommons.postRestResource(OrgUrls.orgRoot, data);
        GrapesCommons.getRestResources(OrgUrls.listNamesUrl, setOrganizationList);
        reloadOrgPage("do",orgName);
        $('#createOrgName').find('input[name="orgname"]').val('');
        $('#createOrgName').find('input[name="corpidlist"]').val('');
        $("#orgModal").modal('hide');
    }
    return;
}

function updateCorporateId(){
    //todo should have a warning the changing and editing ids does not affect names in the artifcats or modules.
    console.log("save stuff 2");
    var oldcorpId = $('#oldCorpId').data("corpId");
    var orgName = $('#oldCorpId').data("orgName");
    var newcorpId = $('#editCorpId').find('input[name="corpId"]').val();

    if (!newcorpId || newcorpId.length === 0) {
        //alert("needs to be not empty or click close to cancel creation");
    }
    else {
        GrapesCommons.postRestResource(OrgUrls.addCorpIdUrl(orgName), '"' + newcorpId + '"',postcallback);
        //potentially the remove could happen before the add it is async so the reshrsh could not show the new id
        GrapesCommons.deleteRestResource(OrgUrls.getCorpIdUrl(orgName, oldcorpId), reloadOrgPage, orgName);
        $('#editCorpId').find('input[name="corpId"]').val('');
        $("#corpIdModal").modal('hide');
    }

}

function addCorporateId(){
    console.log("stupid button did you even press?");
    var listofCorpIds = $('#addCorpIdform').find('input[name="addCorpId"]').val();
    if (listofCorpIds || listofCorpIds.length > 0) {
        GrapesCommons.postRestResource(OrgUrls.addCorpIdUrl($("#deleteOrgBtn").data("orgName")),stringtoarray(listofCorpIds),reloadOrgPage);
        $('#addCorpIdform').find('input[name="addCorpId"]').val('');


        $('#addCorpIdform').find('input[name="addCorpId"]').val('');

    }


}
function postcallback(){
    console.log("i do nothing");
}
function removeOrganization(){
    console.log("DELETE ME muahahahah");
    GrapesCommons.deleteRestResource(OrgUrls.orgUrl($("#deleteOrgBtn").data("orgName")), load);
    $('#deleteOrgModal').modal('hide');
}
/****************************************INIT Functions**************************************************/
function initOrganizationPage() {
    var selectedOrg = "";
    //set list of organization names
    GrapesCommons.getRestResources(OrgUrls.listNamesUrl, setOrganizationList);
    $('#organizationList').click(function () {
        selectedOrg = $("#organizationList option:selected").text();

        //retrieve information on the organization selected from the list.
        GrapesCommons.getRestResources(OrgUrls.orgUrl(selectedOrg), createOrganizationViews);


    });
    console.log($("#organizationList option:selected").text());


    $('#createOrgSaveBtn').click(createOrganization);

    $('#updateOrgSaveBtn').click(updateCorporateId);


    $('#delOrgProceedBtn').click(removeOrganization);
    $('#addOrgCorpIdBtn').click(addCorporateId);



    return;
}



$(document).ready(function () {
    //maybe we call this with a body onload for the page?
    initOrganizationPage();
});