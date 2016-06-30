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
                html += version.commercialName + " " + version.commercialVersion + "\">";
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

 			$.each(data, function(i, name) {
 				html += "<option value=\"";
 				html += name + "\">";
 				html += name + "</option>";
 			});

 			$("#" + moduleNameSelect).empty().append(html);
 		}
 	});
 }

function loadModuleVersions(moduleName, moduleVersionSelect){
	return $.ajax({
		type: "GET",
		accept: {
			json: 'application/json'
		},
		url: "/module/" + moduleName + "/versions",
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
		url: "/artifact/all?groupId=" + artifactGroupId,
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
    var treatedArtifactIds = "/"
	return $.ajax({
		type: "GET",
		accept: {
			json: 'application/json'
		},
		url: "/artifact/all?groupId=" + artifactGroupId +"&version=" + artifactVersion,
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
