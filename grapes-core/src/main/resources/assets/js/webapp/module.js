//todo should make a section for button handlers
//todo look at restructing
//todo wouldn't it be nice if each tab was contained in it own class?
/*******************************COMMONS FUNCTIONS***************************************/
var GrapesModule = {
    reloadPage: function () {
        //todo need add get doc name
        GrapesCommons.getRestResources(ModuleUrls.module(orgName), GrapesModuleViews.createViews);
    },
    initPage: function () {
        var selectedModule = "";
        //set list of module names
        GrapesCommons.getRestResources(ModuleUrls.listNames, GrapesModuleViews.setModuleList);

        $('#moduleList').click(function () {
            $("#moduleVersionList").hide();
            selectedModule = $("#moduleList option:selected").val();


            $(document.body).data("moduleName", selectedModule);
            //retrieve the version numbers fro the module list.
           if(selectedModule!="false") {
               GrapesCommons.getRestResources(ModuleUrls.module(selectedModule), GrapesModuleViews.createModuleVersionList);
           }
        });

        $('#moduleVersionList').click(function () {
            var selectedModuleVersion = $("#moduleVersionList option:selected").val();
            //console.log("made it here");
            $(document.body).data("moduleVersion", selectedModuleVersion);
            //retrieve the version numbers fro the module list.
            if(selectedModuleVersion != "false") {
                GrapesCommons.getRestResources(ModuleUrls.modulePlusVersionUrl(selectedModule, selectedModuleVersion), GrapesModuleViews.createViews);
            }
        });


    }
}

var GrapesModuleHandlers = {
    createModule: function () {
        //todo check handler because unknow should default to true?
        var licName = $('#createNewLicense').find('input[id="licName"]').val();
        var licLongName = $('#createNewLicense').find('input[id="licLongName"]').val();
        var licUrl = $('#createNewLicense').find('input[id="licURL"]').val();
        var licComments = $('#createNewLicense').find('input[id="licComments"]').val();
        var licRegexp = $('#createNewLicense').find('input[id="licRegexp"]').val();


        if (!licName || licName.length === 0
            || !licLongName || licLongName.length === 0) {
            //  alert("license name, longname are required fields");
        }
        else {
            var data = '{ "name": "' + licName + '", "longName": "' + licLongName + '", "comments": "' + licComments
                + '", "regexp": "' + licRegexp + '", "url": "' + licUrl + '", "approved": false }';
            //should refresh list as the call back.
            GrapesCommons.postRestResource(ModuleUrls.root, data, GrapesModule.reloadPage);
//todo put this in rzload page? see product
            GrapesCommons.getRestResources(ModuleUrls.listNames, GrapesModuleViews.setModuleList);
            $("#moduleModal").modal('hide');
        }

    },
    removeModule: function () {

    },
    saveModule: function () {

    }
}
var GrapesModuleViews = {
    setModuleList: function (jsonData) {

        var option = '<option value="false" >Choose a Module</option>';
        for (var i = 0; i < jsonData.length; i++) {
            option += '<option class="grapesOptionSelect" value="' + jsonData[i] + '">' + jsonData[i] + '</option>';
        }
        $('#moduleList').empty().append(option);


    },
    createModuleVersionList: function (jsonData) {
        var option = '<option value="false" >Choose a Version</option>';
        for (var i = 0; i < jsonData.length; i++) {
            option += '<option class="grapesOptionSelect value="' + jsonData[i] + '">' + jsonData[i] + '</option>';
        }
        $('#moduleVersionList').empty().append(option).show();

    },
    createViews: function (jsonData) {
        //console.log("inside create veiws");
        var moduleName = $(document.body).data("moduleName");
        var moduleVersion = $(document.body).data("moduleVersion")
        GrapesModuleTabOverview.createTab(jsonData);
        //GrapesCommons.getRestResources(ModuleUrls.module3rdPartyDependecies(moduleName, moduleVersion), GrapesModuleTabDependcies.createTab);
        //GrapesCommons.getRestResources(ModuleUrls.moduleAncestors(moduleName, moduleVersion), GrapesModuleTabAncestors.createTab);
        GrapesCommons.getRestResources(ModuleUrls.modulePromotionReport(moduleName, moduleVersion), GrapesModuleTabPromotionReport.createTab);
        //GrapesCommons.getRestResources(ModuleUrls.moduleDependeciesReport(moduleName, moduleVersion), GrapesModuleTabDependencyReport.createTab);

    },
    showAdminElements: function () {
        GrapesCommons.setIsAdmin();
        if (GrapesCommons.getIsAdmin()) {
            //todo show stuff here
        }

    }
}


var GrapesModuleTabOverview = {
    createTab: function (json) {

        var tabletitle = "Module Information";
        var moduleId = document.getElementById("moduleId");
        var table = $("<table/>").addClass(' table table-striped');
        $("#moduleId").text(json.name);
        var title = $("<thead><tr><td>" + tabletitle + "</td> </tr></thead>");
        title.addClass("grapesTableTitle");
        table.append(title);

        $.each(json, function (key, val) {
            //console.log("inside create ov tab if one");
            //&& key!== "has" && key!=="uses"

            if (key !== "_id" && key!=="dataModelVersion" && key!=="submodules" && key!=="buildInfo") {
                //console.log("but really wtf");
                var col1 = $("<td>").text(key);
                var col2 = $("<td>").text(val);
                var row = $("<tr/>").append(col1).append(col2);
                table.append(row);

            }
            //console.log("inside create ov tab if two");
            if(key === "submodules" ){
                var col1 = $("<td>").text(key);
                GrapesModuleTabOverview.createSubmoduleInfo(val, table);
            }
            if (key==="buildInfo"){

            }
        })
        $("#moduleOverviewTable").empty().append(table);
        $("#moduleInfo").hide();
        GrapesModuleViews.showAdminElements();
    },
    createSubmoduleInfo: function(submodule,table){
        $.each(submodule, function (key, val) {
            $.each(val, function(k,v){

                if (k === "name" || v=== "version") {

                    var col1 = $("<td>").text("submodule "+k);
                    var col2 = $("<td>").text(v);
                    var row = $("<tr/>").append(col1).append(col2);
                    table.append(row);
                }
                if(k === "submodules" || k==="buildInfo"){
                    GrapesModuleTabOverview.createSubmoduleInfo(v, table);

                }
            })


        })
    }
}
var GrapesModuleTabDependencyReport = {
    createTab: function (json) {
        //todo this should have links that are clickable that take you to the module page
        //todo should seperate 3rd party from in house
        //console.log("I should have a list of depen: " + json.length);
        //console.log(json);
        if (json.length > 0) {
            var table = $('<table/>', {
                class: "table table-striped",
                id: "dependencyReportTable"
            });

            var col1 = $("<td>").text("");
            var col2 = $("<td>").text("GroupId");
            var col3 = $("<td>").text("ArtifactId");
            var col4 = $("<td>").text("Current Version");
            var col5 = $("<td>").text("Most recent Version");
            var col6 = $("<td>").text("Do Not Use");
            var col7 = $("<td>").text("scope");
            var col8 = $("<td>").text("Source");
            var row = $("<tr/>").append(col1).append(col2).append(col3).append(col4).append(col5).append(col6).append(col7).append(col8);
            table.append(row);

            $.each(json, function (key, value) {

                col1 = $("<td>").text(key);
                col2 = $("<td>").text(value.groupId);
                col3 = $("<td>").text(value.artifactId);
                col4 = $("<td>").text(value.currentVersion);
                col5 = $("<td>").text(value.mostRecentVersion);
                col6 = $("<td>").text(value.doNotUse);
                col7 = $("<td>").text(value.Scope);
                col8 = $("<td>").text(value.Source);

                row = $("<tr/>").append(col1).append(col2).append(col3).append(col4).append(col5).append(col6).append(col7).append(col8);
                if(value.doNotUse){
                    row.attr("style","color:red");
                }
                //console.log(key + " " + value);
                //console.log("dep is " + value + value + value);
                table.append(row);
            });
        }
        else {
            $("#reportDependencyInfoMsg").text("There are no dependecies for this module");

        }
        //create tab view here
        $("#dependencyReportList").empty().append(table);
    }
}
var GrapesModuleTabDependcies = {
    createTab: function (json) {

        //todo this should have links that are clickable that take you to the module page
        //todo should seperate 3rd party from in house
        //console.log("I should have a list of depen: " + json.length);
        //console.log(json);
        if (json.length > 0) {
            var table = $('<table/>', {
                class: "table table-striped",
                id: "dependencyTable"
            });
            var col1 = $("<td>").text("");
            var col2 = $("<td>").text("Dependency Source");
            var col3 = $("<td>").text("Target");
            var col4 = $("<td>").text("scope");
            var row = $("<tr/>").append(col1).append(col2).append(col3).append(col4);
            table.append(row);
            $.each(json, function (key, value) {

                col1 = $("<td>").text(key);
                col2 = $("<td>").text(value.source);
                col3 = $("<td>").text(value.target);
                col4 = $("<td>").text(value.scope);
                row = $("<tr/>").append(col1).append(col2).append(col3).append(col4);
                //console.log(key + " " + value);
                //console.log("dep is " + value + value + value);
                table.append(row);
            });
        }
        else {
            $("#dependecyInfoMsg").text("There are no dependecies for this module");
            $("#3rdPartyDependencyList").text("There are no 3rd party dependecies for this module");
        }
        //create tab view here
        $("#dependencyList").empty().append(table);
    }

}
var GrapesModuleTabAncestors = {
    createTab: function (json) {
        //console.log("DID I MAKE IT HERE?????");
        //todo this should have links that are clickable that take you to the module page
        //todo should seperate 3rd party from in house
        //console.log("I should have a list of ancestors: " + json.length + json);
        if (json.length > 0) {
            var table = $('<table/>', {
                class: "table table-striped",
                id: "ancestorTable"
            });
            var col1 = $("<td>").text("");
            var col2 = $("<td>").text("Ancestor Name");
            var col3 = $("<td>").text("version");
            var row = $("<tr/>").append(col1).append(col2).append(col3);
            table.append(row);
            $.each(json, function (key, value) {
                col1 = $("<td>").text(key);
                col2 = $("<td>").text(value);
                col3 = $("<td>").text(value);
                row = $("<tr/>").append(col1).append(col2).append(col3);
                //console.log(key + " " + value);
                //console.log("dep is " + value + value + value);
                table.append(row);
            });
        }
        else {
            $("#ancestorInfoMsg").text("There are no Ancestors for this module");
        }
        //create tab view here
        $("#ancestorsList").empty().append(table);
    },

    processAncestors: function (jsonData) {
        //todo this should have links that are clickable that take you to the module page
        //console.log("I should have a list of anscrots: " + jsonData.length);
        if (jsonData.length > 0) {
            var table = $('<table/>', {
                class: "table table-striped",
                id: "ancestorTable"
            });
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
                //console.log(key + " " + value);
                //console.log("ancestor is " + value.name + value + value.version);
                table.append(row);
            });
        }
        else {
            $("#ancestorInfoMsg").text("There are no ancestors for this module");
        }
        //create tab view here
        $("#ancestorsList").empty().append(table);
    }


}
var GrapesModuleTabPromotionReport = {
    createTab: function (json) {

        //"canBePromoted" : false,
        //    "unPromoted" : "[ModuleC:1.0.0-1, ModuleB:1.0.0-1]",
        //    "promotionPlan" : "[ModuleC:1.0.0-1, ModuleB:1.0.0-1]",
        //    "rootModel" : "Name: ModuleA, Version: 1.0.0-1",
        //    "doNotUseArtifacts" : "0"
        //todo this should have links that are clickable that take you to the module page
        //todo should seperate 3rd party from in house
        //console.log("I should have a promo report: " + json.length);
        console.log(json);
        //if (json.length > 0) {
            $("#dependecyInfoMsg").text("");
            $("#3rdPartyDependencyList").text("");

            var table = $('<table/>', {
                class: "table table-striped",
                id: "PromotionReportTable"
            });
            var col1 = $("<td>").text("Field");

            var col2 = $("<td>").text("Value");
            var row = $("<tr/>").append(col1).append(col2);
            table.append(row);
            $.each(json, function (key, value) {

                col1 = $("<td>").text(key);
                col2 = $("<td>").text(value);

                row = $("<tr/>").append(col1).append(col2);
                //console.log(key + " " + value);
                //console.log("dep is " + value + value + value);
                table.append(row);
            });
        //}
        //else {
        //    $("#dependecyInfoMsg").text("There are no dependecies for this module");
        //    $("#3rdPartyDependencyList").text("There are no 3rd party dependecies for this module");
        //}
        //create tab view here
        $("#promotionReportList").empty().append(table);
    }
}

var ModuleUrls = {
    root: "/module",
    listNames: "/module/names",
    module: function (moduleName) {
        return this.root + "/" + encodeURIComponent(moduleName);
    },
    moduleDependencies: function (moduleName, moduleVersion) {
        return this.root + "/" + encodeURIComponent(moduleName) + "/" + encodeURIComponent(moduleVersion) + "/dependencies";
    },
    module3rdPartyDependecies: function (moduleName, moduleVersion) {

        return this.root + "/"
            + encodeURIComponent(moduleName) + "/"
            + encodeURIComponent(moduleVersion)
            + "/dependencies?scopeTest=true&scopeRuntime=true&showThirdparty=true&showCorporate=false&showSources=false&showLicenses=true&fullRecursive=true";
    },
    moduleDependeciesReport: function (moduleName, moduleVersion) {

        return this.root + "/"
            + encodeURIComponent(moduleName) + "/"
            + encodeURIComponent(moduleVersion)
            + "/dependencies/report?scopeTest=true&scopeRuntime=true&showThirdparty=true&showCorporate=false&showSources=false&showLicenses=true&fullRecursive=true";
    },
    moduleAncestors: function (moduleName, moduleVersion) {

        return this.root + "/" + encodeURIComponent(moduleName) + "/" + encodeURIComponent(moduleVersion) + "/ancestors";
    },
    modulePlusVersionUrl: function (moduleName, moduleVersion) {
        //console.log(this.root + "/" + encodeURIComponent(moduleName) + "/" + encodeURIComponent(moduleVersion));
        return this.root + "/" + encodeURIComponent(moduleName) + "/" + encodeURIComponent(moduleVersion);
    },
    modulePromotionReport: function (moduleName, moduleVersion) {

        return this.root + "/"
            + encodeURIComponent(moduleName) + "/"
            + encodeURIComponent(moduleVersion)
            + "/promotion/report?scopeTest=true&scopeRuntime=true&showThirdparty=true&showCorporate=true&showSources=false&showLicenses=true&fullRecursive=true";
    }
}


$(document).ready(function () {
    //maybe we call this with a body onload for the page?
    GrapesModule.initPage();
});