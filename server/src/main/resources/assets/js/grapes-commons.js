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
 			loadTargetedModuleVersion(moduleName, moduleVersion); 
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
        var stat = $('input[value="filter"]').is(':checked');
        if(stat){
             $("input.options").prop('disabled', false);
        }
        else{
            $("input.options").prop('disabled', 'disabled');
        }
        if ($('input[type=checkbox]').is(':disabled')) {
            $('#s').attr('placeholder', 'Search');
        }
        // uncheck checkboxes on all radio button select
        if(!stat) {
            $('#modules').attr('checked', false);
            $('#artifacts').attr('checked', false)
        }
    });
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
        }
    });
}

/* Get search result call */
function gerSearchResult(){

      // empty the result section before new search
      $("#searchResult").empty();

      // get the text input value
      var searchText = $("#s").val();
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
      html += "<table class=\"table table-bordered table-hover\" id=\"table-of-result\">";

     // make ajax call to search api
	 $.ajax({
		type: "GET",
		accept: {
			json: 'application/json'
		},
		url: getEncodedUrl(searchText, queryParams),
		beforeSend: function () {
            $("#loadingModal").show();
        },
		data: {},
		dataType: "json",
		success: function(data, textStatus) {

		    html += "<thead><tr><th>Modules</th></tr></thead>";
            html += "<tbody>";

            // iterate over modules and construct table body containing the result
            if(data != null && data.modules != null && data.modules.length !== 0) {
                $.each(data.modules, function(i, module) {
                    var obj = getModuleNameAndVersion(module);
                    html += "<tr><td><a href=\"/module/" + obj.name + "/" + obj.version + "\" >" + module + "</a></td></tr>";
                });
            }else {
                html += "<tr><td>No modules found</td></tr>";
            }

            html += "</tr></tbody>";
            html += "</table>";
            html += "<table class=\"table table-bordered table-hover\" id=\"table-of-result-artifacts\">";
            html += "<thead><tr><th>Artifacts</th></tr></thead>";
            html += "<tbody>";

            // iterate over artifacts and construct table body containing artifacts
            if(data != null && data.artifacts != null && data.artifacts.length !== 0) {
                $.each(data.artifacts, function(i, artifact) {
                    html += "<tr><td><a href=\"/artifact/" + artifact + "\">" + artifact + "</a></td></tr>";
                });
            } else {
                html += "<tr><td>No artifacts found</td></tr>";
            }

            html += "</tr></tbody>";
            html += "</table>";
            // hide the waiter modal
            $("#loadingModal").hide();
		    $("#searchResult").empty().append(html);
		}
	});
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
    var nextIndexKey = artifact.indexOf(':', indexKey + 1);
    var lastIndexKey =  artifact.lastIndexOf(':');
    var groupId = artifact.substring(0, indexKey);
    var artifactId = artifact.substring(indexKey + 1, nextIndexKey);
    // get the version if there is an extension of the dependency else substring to the end of the string
    if(lastIndexKey != nextIndexKey) {
        var version = artifact.substring(nextIndexKey + 1, lastIndexKey - 1);
    } else {
        var version = artifact.substring(lastIndexKey + 1);
    }

    return {
        artifactId: artifactId,
        groupId: groupId,
        version: version
    }
}
