function loadOrganizationNames(organizationNameSelect){
	return $.ajax({
		type: "GET",
		accept: {
			json: 'application/json'
		},
		url: "/organization/names",
		data: {},
		dataType: "json",
		success: function(data, textStatus) {
			var html = "<option value=\"-\"></option>";
		
			$.each(data, function(i, name) {
				html += "<option value=\"";
				html += name + "\">";
				html += name + "</option>";
			});

			$("#" + organizationNameSelect).empty().append(html);
		}    
	});  
}

function loadProductNames(productNameSelect){
    return $.ajax({
        type: "GET",
        accept: {
            json: 'application/json'
        },
        url: "/product/names",
        data: {},
        dataType: "json",
        success: function(data, textStatus) {
            var html = "<option value=\"-\"></option>";

            $.each(data, function(i, name) {
                html += "<option value=\"";
                html += name + "\">";
                html += name + "</option>";
            });

            $("#" + productNameSelect).empty().append(html);
        }
    });
}

function loadProductDelivery(productName, productDeliverySelect){
    return $.ajax({
        type: "GET",
        accept: {
            json: 'application/json'
        },
        url: "/product/" + productName + "/deliveries",
        data: {},
        dataType: "json",
        success: function(data, textStatus) {
            var html = "<option value=\"-\"></option>";
            $.each(data, function(i, version) {
                html += "<option value=\"";
                html += version.commercialName + "/" + version.commercialVersion + "\">";
                html += version.commercialName + " " + version.commercialVersion + "</option>";
            });

            $("#" + productDeliverySelect).empty().append(html);
        },
        error: function (xhr, ajaxOptions, thrownError){
            $("#" + productDeliverySelect).empty();
        }
    });
}

function loadModuleNames(moduleNameSelect){
 	return $.ajax({
 		type: "GET",
 		accept: {
 			json: 'application/json'
 		},
 		url: "/module/names",
 		data: {},
 		dataType: "json",
 		success: function(data, textStatus) {
 			var html = "<option value=\"-\"></option>";
 			var moduleName=getNameAndVersion('moduleName');
 			var moduleVersion=getNameAndVersion('moduleVersion');
 			$.each(data, function(i, name) {

 				if(name==moduleName){
 				html += "<option value=\"";
 				html += name + "\" selected>";
 				html += name + "</option>";				
 					}
 				else{
 				html += "<option value=\"";
 				html += name + "\">";
 				html += name + "</option>";
 				}
 			});
 			$("#" + moduleNameSelect).empty().append(html);
 			//loadTargetedModuleVersion(moduleName, moduleVersion);
 		}
 	});
 }

function loadModuleVersions(moduleName, moduleVersionSelect){
	return $.ajax({
		type: "GET",
		accept: {
			json: 'application/json'
		},
		url: "/module/" + encodeURIComponent(moduleName) + "/versions",
		data: {},
		dataType: "json",
		success: function(data, textStatus) {
			var html = "<option value=\"-\"></option>";
			$.each(data, function(i, version) {
				html += "<option value=\"";
				html += version + "\">";
				html += version + "</option>";
			});

			$("#" + moduleVersionSelect).empty().append(html);
		},
		error: function (xhr, ajaxOptions, thrownError){
		    $("#" + moduleVersionSelect).empty();
		}
	});  
}

function loadArtifactGroupIds(artifactGroupIdSelectId){
	return $.ajax({
		type: "GET",
		accept: {
			json: 'application/json'
		},
		url: "/artifact/groupIds",
		data: {},
		dataType: "json",
		success: function(data, textStatus) {
			var groupIds = "<option value=\"-\"></option>";

			$.each(data, function(i, groupId) {
                groupIds += "<option value=\"";
                groupIds += groupId + "\">";
                groupIds += groupId + "</option>";
		    });

			$("#" + artifactGroupIdSelectId).empty().append(groupIds);
		}
	});
}

function loadArtifactVersions(artifactGroupId, artifactVersionSelectId){
    var treatedVersions = "/";
	return $.ajax({
		type: "GET",
		accept: {
			json: 'application/json'
		},
		url: "/artifact/all?groupId=" + encodeURIComponent(artifactGroupId),
		data: {},
		dataType: "json",
		success: function(data, textStatus) {
			var versions = "<option value=\"-\"></option>";

            var sortedVersions = [];
            $.each(data, function(i, artifact) {
                if(treatedVersions.indexOf("/" + artifact.version + "/") < 0){
                    sortedVersions.push(artifact.version);
                    treatedVersions += artifact.version + "/";
                }
            });

            sortedVersions.sort();

			$.each(sortedVersions, function(i, version) {
                    versions += "<option value=\"";
                    versions += version + "\">";
                    versions += version + "</option>";
			});

			$("#" + artifactVersionSelectId).empty().append(versions);
		}
	});
}

function loadArtifactArtifactId(artifactGroupId, artifactVersion, artifactArtifactIdSelectId){
    var treatedArtifactIds = "/";
	return $.ajax({
		type: "GET",
		accept: {
			json: 'application/json'
		},
		url: "/artifact/all?groupId=" + encodeURIComponent(artifactGroupId) +"&version=" + artifactVersion,
		data: {},
		dataType: "json",
		success: function(data, textStatus) {
			var artifactIds = "<option value=\"-\"></option>";

            var sortedArtifactId = [];
            $.each(data, function(i, artifact) {
                if(treatedArtifactIds.indexOf("/" + artifact.artifactId + "/") < 0){
                    sortedArtifactId.push(artifact.artifactId);
                    treatedArtifactIds += artifact.artifactId + "/";
                }
            });

            sortedArtifactId.sort();

			$.each(sortedArtifactId, function(i, artifactId) {
                    artifactIds += "<option value=\"";
                    artifactIds += artifactId + "\">";
                    artifactIds += artifactId + "</option>";
			});

			$("#" + artifactArtifactIdSelectId).empty().append(artifactIds);
		}
	});
}

function loadLicensesNames(licenseNamesSelectId){
	return $.ajax({
		type: "GET",
		accept: {
			json: 'application/json'
		},
		url: "/license/names",
		data: {},
		dataType: "json",
		success: function(data, textStatus) {
			var licenses = "<option value=\"-\"></option>";

			$.each(data, function(i, licenseName) {
                licenses += "<option value=\"";
                licenses += licenseName + "\">";
                licenses += licenseName + "</option>";
		    });

			$("#" + licenseNamesSelectId).empty().append(licenses);
		}
	});
}

/* Disable or enable checkbox option depending on user selection */
function filterRadioOptions(radio){
    $(radio).change(function () {
        var status = $('input[value="filter"]').is(':checked');
       checkRadioButtonsStatus(status);
       // uncheck checkboxes on all radio button select
       if(!status) {
           $('#modules').attr('checked', false);
           $('#artifacts').attr('checked', false)
       }
    });
}

/* Enable/disable checkbox section depending on radio button select */
function checkRadioButtonsStatus(status) {
    if(status){
         $("input.options").prop('disabled', false);
    }
    else{
        $("input.options").prop('disabled', 'disabled');
    }
    if ($('input[type=checkbox]').is(':disabled')) {
        $('#s').attr('placeholder', 'Search');
    }
}

/* Toggle placeholder text depending on user checkbox selection */
function filterCheckBoxOptions(checkbox){
    $(checkbox).change(function() {
        if($('input[value=artifacts]').is(':checked') && $('input[value=modules]').is(':checked')) {
            $('#s').attr('placeholder', 'Search modules and artifacts');
        } else if ($('input[value=modules]').is(':checked')) {
            $('#s').attr('placeholder', 'Search modules');
        } else  if($('input[value=artifacts]').is(':checked')) {
            $('#s').attr('placeholder', 'Search artifacts');
        } else {
            $('#s').attr('placeholder', 'Search');
            $("#all").click();
        }
    });
}

/* Get search result call */
function getSearchResult(){

      // empty the result section before new search
      $("#searchResult").empty();

      // get the text input value
      var searchText = $("#s").val().trim();

      // Do not make request if the search criteria contains space
      if(searchText.indexOf(' ') !== -1) {
        return;
      }

      var queryParams = "";

      // check for selected checkbox option to include in the request
      if(!$('input[value=modules]').is(':checked') && !$('input[value=all]').is(':checked')){
        queryParams += "modules=false" + "&"
      }

      if(!$('input[value=artifacts]').is(':checked') && !$('input[value=all]').is(':checked')){
        queryParams += "artifacts=false" + "&"
      }

      // construct response table containing modules and artifacts
      var html= "";

     // make ajax call to search api
	 $.ajax({
		type: "GET",
		accept: {
			json: 'application/json'
		},
		url: getEncodedUrl(searchText, queryParams),
		beforeSend: function () {
            $(".overlay").show();
        },
		data: {},
		dataType: "json",
		success: function(data, textStatus) {
            var modulesData;
            var artifactsData;
            if(data != null) {
		        modulesData = data.modules;
		        artifactsData = data.artifacts;
		    }

            if(modulesData != null && ($('input[value=all]').is(':checked') || $('input[value=modules]').is(':checked'))){
                html += "<table class=\"table table-bordered table-hover\" id=\"table-of-result\">";
                html += "<thead><tr><th>Modules</th></tr></thead>";
                html += "<tbody>";

                // iterate over modules and construct table body containing the result
                if(modulesData.length !== 0) {
                    if(modulesData.length === 1 && modulesData[0] === "TOO_MANY_RESULTS") {
                        html += "<tr><td style=\"color: red\">The search generated too many results. You can refine your search criteria by providing group id and classifier id such as javax.mail:mail:1</td></tr>";
                    } else {
                        $.each(modulesData, function(i, module) {
                            var obj = getModuleNameAndVersion(module);
                            html += "<tr><td>" +
                                getDataBrowserButton('navigateToDataBrowserModule') +
                                " <a href=\"/module/" + encodeURIComponent(obj.name) + "/" + encodeURIComponent(obj.version) + "\" >" + module + "</a><span></span></td></tr>";
                        });
                    }
                }else {
                    html += "<tr><td>Nothing found</td></tr>";
                }

                html += "</tr></tbody>";
                html += "</table>";
            }

            if(artifactsData != null && ($('input[value=all]').is(':checked') || $('input[value=artifacts]').is(':checked'))){
                html += "<table class=\"table table-bordered table-hover\" id=\"table-of-result-artifacts\">";
                html += "<thead><tr><th>Artifacts</th></tr></thead>";
                html += "<tbody>";

                // iterate over artifacts and construct table body containing artifacts
                if(artifactsData.length !== 0) {
                    if(artifactsData.length === 1 && artifactsData[0] === "TOO_MANY_RESULTS") {
                        html += "<tr><td style=\"color: red\">The search generated too many results. You can refine your search criteria by providing group id and classifier id such as javax.mail:mail:1</td></tr>";
                    } else {
                        $.each(artifactsData, function(i, artifact) {
                            html += "<tr><td>"+
                                getDataBrowserButton('navigateToDataBrowserArtifact') +
                                " <a href=\"/artifact/" + encodeURIComponent(artifact) + "\">" + artifact + "</a><span></span></td></tr>";
                        });
                    }
                } else {
                    html += "<tr><td>Nothing found</td></tr>";
                }

                html += "</tr></tbody>";
                html += "</table>";
            }
            // hide the waiter modal
            $(".overlay").hide();
		    $("#searchResult").empty().append(html);
		}
	});
}

function getDataBrowserButton(fnName) {
    return "<button class='btn btn-inverse' " +
            " onclick=\"" + fnName + "(this); return false;\"" +
            " title='Select in Data Browser'>" +
            "<span class='icon-list icon-white' title='Select in Data Browser'></span>" +
            "</button>";
}

/* Return encoded url with or without query params depending on checkbox selection */
function getEncodedUrl(searchText, queryParams) {
    var url = "";
    if (queryParams.length !== 0) {
        url = "/search/" + encodeURIComponent(searchText) + "?" + queryParams;
    } else {
        url = "/search/" + encodeURIComponent(searchText);
    }
    return url;
}

/* Extract the module name and version */
function getModuleNameAndVersion(module) {

    var indexKey = module.indexOf(':');
    var nextIndexKey = module.indexOf(':', indexKey + 1);
    var moduleName = module.substring(0, nextIndexKey);
    var moduleVersion = module.substring(nextIndexKey + 1);

    return {
        name: moduleName,
        version: moduleVersion
    }
}

/* Extract the artifactId, groupId and version of artifact from link object */
function getArtifactGAVC(artifactObj) {

    var artifact = artifactObj.text.trim();
    var indexKey = artifact.indexOf(':');
    var artifactIndexKey = artifact.indexOf(':', indexKey + 1);
    var versionIndexKey =  artifact.indexOf(':', artifactIndexKey + 1);
    var groupId = artifact.substring(0, indexKey);
    var artifactId = artifact.substring(indexKey + 1, artifactIndexKey);
    var version = artifact.substring(artifactIndexKey + 1, versionIndexKey);

    return {
        artifactId: artifactId,
        groupId: groupId,
        version: version
    }
}

/* Enable searching with enter button if the length validation is ok */
$("input[type='text']").keyup(function(e) {
    //input#s.form-control
    if ((this.value.trim().length > 2) && ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13))) {
        e.preventDefault();
        $("input[type='submit']").click();
    }
    if (this.value.length == 0) {
        $("#submitButton").prop("disabled", true);
    }
});

/* Browser back button handling*/
$(document).ready(function(event) {
    if(document.location.pathname === "/search"){
        // if the input value has 3 chars or more - enable
        if($("#s").val().length > 2) {
            $("#submitButton").prop("disabled", false);
        }
        checkRadioButtonsStatus($('input[value="filter"]').is(':checked'));
    }
});

$('#searchForm').bootstrapValidator({
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            s: {
                validators: {
                    stringLength: {
                        message: 'The search criteria must contain at least 3 characters',
                        min: minCount
                    },
                    regexp: {
                        regexp: /^\s*([^\s]+)*\s*$/,
                        message: 'Spaces are not allowed!'
                    }
                }
            }
        }
});

/* Return the minimum length of the user input (3 trimmed chars)*/
function minCount(value, validator, $field) {
    var actual = value.length;
    var trimmed = value.trim().length;
    return (actual - trimmed) + 3;
}

/* Navigate to search page with checkbox checked depending on the selected section */
function navigateToSearch(el) {
    var checkBox;
    if(el.id == "searchModules") {
        checkBox = '#modules';
    } else {
        checkBox = '#artifacts';
    }

    $("body").load("/search", function(data){
        document.open();
        document.write(data);
        document.close();
        setTimeout(function(){
            $('#s').focus();
            $('input[value="filter"]').click();
            $(checkBox).attr('checked', true);
        }, 300);
    });

    window.history.pushState("", "", "/search");
}

/* Navigate to artifact data browser section */
function navigateToDataBrowserArtifact(el) {
    // Get the first anchor element (artifact gavc)
    var elText = getNextAnchor(el);
    var artifact = getArtifactGAVC(elText);

    navigateToArtifactInDB(artifact.groupId, artifact.artifactId, artifact.version, 'targets', '#artifactOverviewButton');
}

function navigateToArtifactInDB(groupId, artifactId, version) {
    $("body").load("/webapp", function() {
        $(function(){
            $('#artifactButton').click();
        });
    });
    setTimeout(function() {
        getArtifactTarget(groupId, artifactId, version, 'targets', '#artifactOverviewButton');
    }, 400);
    window.history.pushState("", "", "/webapp");

}

/* Navigate to module data browser section */
function navigateToDataBrowserModule(el) {
    // Get the first anchor element (module name and version)
    var elText = getNextAnchor(el);
    var module = getModuleNameAndVersion(elText.text);

    navigateToModuleInDB(module.name, module.version);
}

function navigateToModuleInDB(name, version) {
    $("body").load("/webapp", function() {
        $(function(){
            $('#moduleButton').click();
        });
    });
    setTimeout(function() {
        getModuleTarget(name, version,'false','targets');
    }, 400);
    window.history.pushState("", "", "/webapp");
}

/* Get the anchor next to current anchor */
function getNextAnchor(el){
    return $(el).parent().children('a:eq(0)').get(0);
}