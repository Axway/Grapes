/**************************************************************************************************/
/*          Fill web-app with actions and filters regarding the targeted object type               */
/**************************************************************************************************/
function displayOrganizationOptions(){
    $("#targets").empty();
    cleanAction();
    var organizationIds = "<div class=\"control-group\">\n";
    organizationIds += "   <label class=\"control-label\" for=\"organizationName\" style=\"width: auto;\">name: </label>\n";
    organizationIds += "      <div class=\"controls\"  style=\"margin-left: 75px;\"><select id=\"organizationName\"></select></div>\n";
    organizationIds += "</div>\n";
    $("#ids").empty().append(organizationIds);
    $("#filters").empty();
    var organizationActions = "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n";
    organizationActions += "   <button type=\"button\" class=\"btn btn-danger action-button\" style=\"margin:2px;\" onclick='createOrganization();'>New</button>\n";
    organizationActions += "   <button type=\"button\" class=\"btn btn-danger action-button\" style=\"margin:2px;\" onclick='getOrganizationOverview();'>Overview</button>\n";
    organizationActions += "</div>\n";
    $("#action").empty().append(organizationActions);
    $("#action-perform").empty();
    loadOrganizationNames("organizationName");

    $("#search").empty().append("<button type=\"button\" class=\"btn btn-primary\" style=\"margin:2px;\"  onclick='getOrganizationList(\"organizationName\", \"targets\");'><i class=\"icon-search icon-white\"></i></button>");
}

function displayProductOptions(){
    $("#targets").empty();
    cleanAction();
    var productIds = "<div class=\"control-group\">\n";
    productIds += "   <label class=\"control-label\" for=\"productName\" style=\"width: auto;\">name: </label>\n";
    productIds += "      <div class=\"controls\"  style=\"margin-left: 75px;\"><select id=\"productName\"></select></div>\n";
    productIds += "</div>\n";
    productIds += "<div class=\"control-group\">\n";
    productIds += "   <label class=\"control-label\" for=\"productDelivery\" style=\"width: auto;\">delivery: </label>\n";
    productIds += "      <div class=\"controls\"  style=\"margin-left: 75px;\"><select id=\"productDelivery\"></select></div>\n";
    productIds += "</div>\n";
    $("#ids").empty().append(productIds);
    $("#filters").empty();
    var productActions = "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n";
    productActions += "   <button type=\"button\" class=\"btn btn-danger action-button\" style=\"margin:2px;\" onclick='createProduct();'>New Product</button>\n";
    productActions += "   <button type=\"button\" class=\"btn btn-danger action-button\" style=\"margin:2px;\" onclick='getProductOverview();'>Overview</button>\n";
    productActions += "   <button type=\"button\" class=\"btn btn-danger action-button\" style=\"margin:2px;\" onclick='deleteProduct();'>Delete</button>\n";
    productActions += "</div>\n";
    $("#action").empty().append(productActions);
    $("#action-perform").empty();
    loadProductNames("productName");

    $("#productName").change(function () {
        loadProductDelivery($("#productName").val(), "productDelivery");
    });

    $("#search").empty().append("<button type=\"button\" class=\"btn btn-primary\" style=\"margin:2px;\"  onclick='getProductList(\"productName\", \"productDelivery\", \"targets\");'><i class=\"icon-search icon-white\"></i></button>");
}

function displayModuleOptions(){
    $("#targets").empty();
    cleanAction();
	var moduleIds = "<div class=\"control-group\">\n";
	moduleIds += "   <label class=\"control-label\" for=\"moduleName\" style=\"width: auto;\">name: </label>\n";
	moduleIds += "   <div class=\"controls\"  style=\"margin-left: 75px;\"><select id=\"moduleName\"></select></div>\n";
	moduleIds += "</div>\n";
	moduleIds += "<div class=\"control-group\">\n";
	moduleIds += "   <label class=\"control-label\" for=\"moduleVersion\" style=\"width: auto;\">version: </label>\n";
	moduleIds += "   <div class=\"controls\" style=\"margin-left: 75px;\"><select id=\"moduleVersion\"></select></div>\n";
	moduleIds += "</div>\n";
	$("#ids").empty().append(moduleIds);
	var moduleFilters = "<div class=\"row-fluid\">\n";
	moduleFilters += "   <label>\n";
	moduleFilters += "      <input id=\"promoted\" type=\"checkbox\"> promoted\n";
	moduleFilters += "   </label>\n";
	moduleFilters += "</div>\n";
	$("#filters").empty().append(moduleFilters);
	var moduleActions = ""
	moduleActions += "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n";
	moduleActions += "   <div id=\"moduleActions\" class=\"row-fluid\">\n";
	moduleActions += "      <button type=\"button\" class=\"btn btn-info action-button\" style=\"margin:2px;\" onclick='getModuleOverview();' id=\"overviewButton\">Overview</button>\n";
	moduleActions += "      <button type=\"button\" class=\"btn btn-info action-button\" style=\"margin:2px;\" onclick='getModuleDependencies();'>Dependencies</button>\n";
	moduleActions += "      <button type=\"button\" class=\"btn btn-info action-button\" style=\"margin:2px;\" onclick='getModuleThirdParty();'>Third Party</button>\n";
	moduleActions += "      <button type=\"button\" class=\"btn btn-info action-button\" style=\"margin:2px;\" onclick='getModuleAncestors();'>Ancestors</button>\n";
	moduleActions += "      <button type=\"button\" class=\"btn btn-info action-button\" style=\"margin:2px;\" onclick='getModulePromotionReport();'>Promotion Report</button>\n";
	moduleActions += "      <button type=\"button\" class=\"btn btn-info action-button\" style=\"margin:2px;\" onclick='displayModuleLicenseOptions();'>Licenses</button>\n";
	moduleActions += "   </div>\n";
	moduleActions += "</div>\n";
	$("#action").empty().append(moduleActions);
	$("#action-perform").empty();
	loadModuleNames("moduleName");

	$("#moduleName").change(function () {
		loadModuleVersions($("#moduleName").val(), "moduleVersion");
	});

	$("#search").empty().append("<button type=\"button\" class=\"btn btn-primary\" style=\"margin:2px;\"  onclick='getModuleList(\"moduleName\", \"moduleVersion\", \"promoted\", \"targets\");'><i class=\"icon-search icon-white\"></i></button>");
}

function displayArtifactOptions(){
    $("#targets").empty();
    cleanAction();
	var artifactIds = "<div class=\"control-group\">\n";
	artifactIds += "   <label class=\"control-label\" for=\"artifactGroupId\" style=\"width: auto;\">groupId: </label>\n";
	artifactIds += "   <div class=\"controls\"  style=\"margin-left: 75px;\"><select id=\"groupId\"></select></div>\n";
	artifactIds += "</div>\n";
	artifactIds += "<div class=\"control-group\">\n";
    artifactIds += "   <label class=\"control-label\" for=\"artifactVersion\" style=\"width: auto;\">version: </label>\n";
    artifactIds += "   <div class=\"controls\"  style=\"margin-left: 75px;\"><select id=\"version\"></select></div>\n";
    artifactIds += "</div>\n";
	artifactIds += "<div class=\"control-group\">\n";
	artifactIds += "   <label class=\"control-label\" for=\"artifactArtifactId\" style=\"width: auto;\">artifactId: </label>\n";
	artifactIds += "   <div class=\"controls\"  style=\"margin-left: 75px;\"><select id=\"artifactId\"></select></div>\n";
	artifactIds += "</div>\n";
    $("#ids").empty().append(artifactIds);
	var artifactFilters = "<div class=\"row-fluid\">\n";
	artifactFilters += "   <label>\n";
	artifactFilters += "      <input id=\"doNotUse\" type=\"checkbox\"> do not use\n";
	artifactFilters += "   </label>\n";
	artifactFilters += "</div>\n";
	$("#filters").empty().append(artifactFilters);
	var artifactActions = "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n";
	artifactActions += "   <button type=\"button\" class=\"btn btn-success action-button\" style=\"margin:2px;\" onclick='getArtifactOverview();'>Overview</button>\n";
	artifactActions += "   <button type=\"button\" class=\"btn btn-success action-button\" style=\"margin:2px;\" onclick='getArtifactAncestors();'>Ancestors</button>\n";
	artifactActions += "   <button type=\"button\" class=\"btn btn-success action-button\" style=\"margin:2px;\" onclick='doNotUseArtifact();'>Do not use</button>\n";
	artifactActions += "   <button type=\"button\" class=\"btn btn-success action-button\" style=\"margin:2px;\" onclick='getArtifactLicenses();'>Licenses</button>\n";
	artifactActions += "</div>\n";
	$("#action").empty().append(artifactActions);
	$("#action-perform").empty();
	loadArtifactGroupIds("groupId");

	$("#groupId").change(function () {
		loadArtifactVersions($("#groupId").val(), "version");
		$("#artifactId").empty();
	});

	$("#version").change(function () {
		loadArtifactArtifactId($("#groupId").val(),$("#version").val(), "artifactId");
	});

	$("#search").empty().append("<button type=\"button\" class=\"btn btn-primary\" style=\"margin:2px;\"  onclick='getArtifactList(\"groupId\", \"artifactId\", \"version\", \"doNotUse\", \"targets\");'><i class=\"icon-search icon-white\"></i></button>");
}

function displayLicenseOptions(){
    $("#targets").empty();
    cleanAction();
	var licenseIds = "<div class=\"control-group\">\n";
	licenseIds += "   <label class=\"control-label\" for=\"licenseName\" style=\"width: auto;\">name: </label>\n";
	licenseIds += "      <div class=\"controls\"  style=\"margin-left: 75px;\"><select id=\"licenseName\"></select></div>\n";
	licenseIds += "</div>\n";
	$("#ids").empty().append(licenseIds);
	var licenseFilters = "<form class=\"form-vertical\">\n";
	licenseFilters += "   <label class=\"radio\">\n";
	licenseFilters += "      <input type=\"radio\" name=\"gavc\" value=\"to be validated\" id=\"toBeValidated\"> to be validated\n";
	licenseFilters += "   </label>\n";
	licenseFilters += "   <label class=\"radio\">\n";
	licenseFilters += "      <input type=\"radio\" name=\"gavc\" value=\"approved\" id=\"validated\"> approved\n";
	licenseFilters += "   </label>\n";
	licenseFilters += "   <label class=\"radio\">\n";
	licenseFilters += "      <input type=\"radio\" name=\"gavc\" value=\"rejected\" id=\"unvalidated\"> rejected\n";
	licenseFilters += "   </label>\n";
	licenseFilters += "</form>\n";
	$("#filters").empty().append(licenseFilters);
	var licenseActions = "<div class=\"btn-group\" data-toggle=\"buttons-radio\">\n";
	licenseActions += "   <button type=\"button\" class=\"btn btn-warning action-button\" style=\"margin:2px;\" onclick='createLicense();'>New</button>\n";
	licenseActions += "   <button type=\"button\" class=\"btn btn-warning action-button\" style=\"margin:2px;\" onclick='getLicenseOverview();'>Overview</button>\n";
	licenseActions += "   <button type=\"button\" class=\"btn btn-warning action-button\" style=\"margin:2px;\" onclick='approveLicense();'>Approve</button>\n";
	licenseActions += "   <button type=\"button\" class=\"btn btn-warning action-button\" style=\"margin:2px;\" onclick='rejectLicense();'>Reject</button>\n";
	licenseActions += "</div>\n";
	$("#action").empty().append(licenseActions);
	$("#action-perform").empty();
	loadLicensesNames("licenseName");

	$("#search").empty().append("<button type=\"button\" class=\"btn btn-primary\" style=\"margin:2px;\"  onclick='getLicenseList(\"licenseName\", \"toBeValidated\", \"validated\", \"unvalidated\", \"targets\");'><i class=\"icon-search icon-white\"></i></button>");
}
/**************************************************************************************************/
/*             Add optional actions to the web-app regarding the selected action                  */
/**************************************************************************************************/
function displayModuleLicenseOptions(){
	var moduleLicenseOptionalActions = ""
	moduleLicenseOptionalActions += "<button id=\"fullRecursive\" type=\"button\" class=\"btn btn-primary action-button\" data-toggle=\"button\" onclick='updateLicenseReport();' >On full corporate tree</button>\n";
	moduleLicenseOptionalActions += "<a href=\"#\" class=\"btn btn-primary action-button export\">CSV export</a>\n";
	$("#optional-action").empty().append(moduleLicenseOptionalActions);

	getModuleLicenses();

	$(".export").on('click', function (event) {
        exportTableToCSV.apply(this, [$('#table'), 'export.csv']);
    });

}

/********************************************************************/
/*          Fill web-app targets regarding the filters               */
/********************************************************************/
function getOrganizationList(organizationNameFieldId, targetedFieldId){
    $("#" + targetedFieldId).empty();
    $('.alert').hide();
    cleanAction();

    var organizationName = $("#" + organizationNameFieldId).val();

    $.ajax({
    		type: "GET",
    		accept: {
    			json: 'application/json'
    		},
    		url: "/organization/" + organizationName ,
    		data: {},
    		dataType: "json",
    		success: function(data, textStatus) {
    			var html = "<label class=\"radio\">"
                html += "<input type=\"radio\" name=\"organizationId\" value=\""+ data.name+ "\" onclick=\"cleanAction()\">";
                html += data.name;
                html += "</label>"

    			$("#" + targetedFieldId).append(html);
    		}
    }).done(function(){
        setTimeout(function(){
              $("input:radio[name=organizationId]:first").attr('checked', true);
            }, 500);
    });
}

function getProductList(productNameFieldId, productDeliveryFieldId, targetedFieldId){
    $("#" + targetedFieldId).empty();
    $('.alert').hide();
    cleanAction();
    var productName = $("#" + productNameFieldId).val();
    var productDelivery = $("#" + productDeliveryFieldId).val();

    if(productName == '-' || productName == null){
        return;
    }

    var html = "";
    if(productDelivery != '-' && productDelivery != null){
        html += "<label class=\"radio\">"
        html += "<input type=\"radio\" name=\"productRadio\" value=\""+ productDelivery+ "\" onclick=\"cleanAction()\">";
        html += productDelivery;
        html += "</label>";
        $("#" + targetedFieldId).append(html);
        $("input:radio:first").attr('checked', true);
        return;
    }


    html += "<label class=\"radio\">"
    html += "<input type=\"radio\" name=\"productRadio\" value=\""+ productName+ "\" onclick=\"cleanAction()\">";
    html += productName;
    html += "</label>";

    $.ajax({
        type: "GET",
        accept: {
            json: 'application/json'
        },
        url: "/product/" + productName + "/deliveries",
        data: {},
        dataType: "json",
        success: function(data, textStatus) {
            $.each(data, function(i, delivery) {
                html += "<label class=\"radio\">"
                html += "<input type=\"radio\" name=\"productRadio\" value=\""+ delivery+ "\" onclick=\"cleanAction()\">";
                html += delivery;
                html += "</label>";
            });

            $("#" + targetedFieldId).append(html);
        }
    }).done(function(){
        setTimeout(function(){
            $("input:radio:first").attr('checked', true);
        }, 500);
    });
}

function getModuleList(moduleNameFieldId, moduleVersionFieldId, promotedFieldId, targetedFieldId){
    $("#" + targetedFieldId).empty();
    $('.alert').hide();
    cleanAction();
    var moduleName = $("#" + moduleNameFieldId).val();
    var moduleVersion = $("#" + moduleVersionFieldId).val();

    var queryParams = "";
    if(moduleName != '-' && moduleName != null){
        queryParams += "name="+ moduleName +"&"
    }
    if(moduleVersion != '-' && moduleVersion != null){
        queryParams += "version="+ moduleVersion +"&"
    }
    if($("#" + promotedFieldId).is(':checked')){
        queryParams += "promoted=true"
    }

    $.ajax({
        type: "GET",
        accept: {
            json: 'application/json'
        },
        url: "/module/all?" + queryParams ,
        data: {},
        dataType: "json",
        success: function(data, textStatus) {
            var html = "";
            $.each(data, function(i, module) {
                var moduleId = module.name + ":" + module.version;
                html += "<label class=\"radio\">"
                html += "<input type=\"radio\" name=\"moduleId\" value=\""+ moduleId+ "\" onclick=\"cleanAction()\">";
                html += moduleId;
                html += "</label>"
            });

            $("#" + targetedFieldId).append(html);
        }
    }).done(function(){
        setTimeout(function(){
            $("input:radio[name=moduleId]:first").attr('checked', true);
        }, 500);
    });
}

function getArtifactList(groupIdFieldId, artifactIdFieldId, versionFieldId, doNotUseFieldId, targetedFieldId){
    $("#" + targetedFieldId).empty();
    $('.alert').hide();
    cleanAction();
    var groupId = $("#" + groupIdFieldId).val();
    var artifactId = $("#" + artifactIdFieldId).val();
    var version = $("#" + versionFieldId).val();

    var queryParams = "";
    if(groupId != '-' && groupId != null){
        queryParams += "groupId="+ groupId +"&"
    }
    if(artifactId != '-' && artifactId != null){
        queryParams += "artifactId="+ artifactId +"&"
    }
    if(version != '-' && version != null){
        queryParams += "version="+ version +"&"
    }
    if($("#" + doNotUseFieldId).is(':checked')){
        queryParams += "doNotUse=true"
    }

    $.ajax({
    		type: "GET",
    		accept: {
    			json: 'application/json'
    		},
    		url: "/artifact/all?" + queryParams ,
    		data: {},
    		dataType: "json",
    		success: function(data, textStatus) {
    			var html = "";
    			$.each(data, function(i, artifact) {
    			    var gavc = artifact.groupId + ":" + artifact.artifactId + ":" +artifact.version + ":";
    			    if(typeof artifact.classifier!='undefined'){
                        gavc+= artifact.classifier;
    			    }
                    gavc+= ":";
    			    if(typeof artifact.extension!='undefined'){
                        gavc+= artifact.extension;
    			    }

    			    html += "<label class=\"radio\">"
    				html += "<input type=\"radio\" name=\"gavc\" value=\""+ gavc+ "\" onclick=\"cleanAction()\">";
    				html += gavc;
    				html += "</label>"
    			});

    			$("#" + targetedFieldId).append(html);
    		}
    }).done(function(){
          setTimeout(function(){
                $("input:radio[name=gavc]:first").attr('checked', true);
              }, 500);
      });
}

function getLicenseList(licenseNameFieldId, toBeValidatedFieldId, validatedFieldId, unvalidatedFieldId, targetedFieldId){
    $("#" + targetedFieldId).empty();
    $('.alert').hide();
    cleanAction();

    var licenceName = $("#" + licenseNameFieldId).val();

    var queryParams = "";
    if(licenceName != '-' && licenceName != null){
        queryParams += "licenseId="+licenceName+"&";
    }
    if($("#" + toBeValidatedFieldId).is(':checked')){
        queryParams += "toBeValidated=true&"
    }
    if($("#" + validatedFieldId).is(':checked')){
        queryParams += "approved=true&"
    }
    if($("#" + unvalidatedFieldId).is(':checked')){
        queryParams += "approved=false"
    }

    $.ajax({
    		type: "GET",
    		accept: {
    			json: 'application/json'
    		},
    		url: "/license/names?" + queryParams ,
    		data: {},
    		dataType: "json",
    		success: function(data, textStatus) {
    			var html = "";
    			$.each(data, function(i, licenseName) {
    			    html += "<label class=\"radio\">"
    				html += "<input type=\"radio\" name=\"licenseId\" value=\""+ licenseName+ "\" onclick=\"cleanAction()\">";
    				html += licenseName;
    				html += "</label>"
    			});

    			$("#" + targetedFieldId).append(html);
    		}
    }).done(function(){
        setTimeout(function(){
              $("input:radio[name=licenseId]:first").attr('checked', true);
            }, 500);
    });
}

/********************************************************/
/*               Actions definitions                    */
/********************************************************/
function createOrganization(){
    $('#organizationEdition').find('#inputOrganizationName').val("");
	$("#organizationEdition").modal('show');
}

function organizationSave(){
    $.ajax({
        url: "/organization",
        method: 'POST',
        contentType: 'application/json',
        data: '{ "name": "'+$('#inputOrganizationName').val()+'", "corporateGroupIdPrefixes": []}',
        error: function(xhr, error){
            alert("The action cannot be performed: status " + xhr.status);
        }
    }).done(function(){
            cleanAction();
            updateOrganizationOptions();
    });
}

function getOrganizationOverview(){
    if($('input[name=organizationId]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }
	var organizationId = $('input[name=organizationId]:checked', '#targets').val();

	$.ajax({
            type: "GET",
            url: "/organization/"+ organizationId,
            data: {},
            dataType: "json",
            success: function(data, textStatus) {
                var html = "<h3>Organization</h3><br/>\n";
                html += "<p><strong>Name:</strong>"+data.name+"</p>\n";
                html += "<table class=\"table table-bordered table-hover\" id=\"table-of-result\">\n";
                html += "<thead><tr><td>Corporate GroupId Prefixes</td></tr></thead>\n";
                html += "<tbody>\n";
                $.each(data.corporateGroupIdPrefixes, function(i,corporateGid) {
                    html += "<tr id=\""+corporateGid+"-row\"><td onclick=\"removeCorporateGidAction('"+corporateGid+"');\">" + corporateGid + "</td></tr>\n";
                });
                html += "</tbody>\n";

                $("#results").empty().append(html);
            }
        })

    var html ='<form><fieldset><input id="newCorporateGid" style=\"margin:2px;\" type="text" placeholder="Corporate groupId prefix">\n<button type=\"button\" class=\"btn\" style=\"margin:2px;\" onclick=\"addCorporateGid();\">add</button>\n</fieldset></form>';
    $("#extra-action").empty().append(html);
    $("#extra-action").append("<button type=\"button\" class=\"btn btn-inverse\" style=\"margin:2px;\" onclick=\"deleteOrganization('"+organizationId+"');\">Delete</button>\n");

}

function addCorporateGid(){
     var corporateGid = $("#newCorporateGid").val();
     var organization = $('input[name=organizationId]:checked', '#targets').val();

     $.ajax({
             type: "POST",
             url: "/organization/" + organization + "/corporateGroupIds" ,
             data: corporateGid,
             dataType: "html",
             error: function(xhr, error){
                 alert("The action cannot be performed: status " + xhr.status);
             },
             success : updateOrganization()
         }).done(updateOrganization());
 }

 function removeCorporateGidAction(corporateGid){
     var organization = $('input[name=organizationId]:checked', '#targets').val();
     var html ="<div class=\"row-fluid\">The corporate groupId prefix <strong>"+corporateGid+"</strong> is about to be remove from organization "+organization+".</div>\n";
     html += "\n<div class=\"row-fluid\">Do you really want to remove this association?</div>\n";

     $("#removeAssociationModal-text").empty().append(html);
     $('#removeAssociationModal-button').attr('onclick', 'removeCorporateGid(\''+corporateGid+'\');');
     $('#removeAssociationModal').modal('show');
 }

function removeCorporateGid(corporateGid){
     var organization = $('input[name=organizationId]:checked', '#targets').val();

     $.ajax({
             type: "DELETE",
             url: "/organization/" + organization + "/corporateGroupIds" ,
             data: corporateGid,
             dataType: "html",
             error: function(xhr, error){
                 alert("The action cannot be performed: status " + xhr.status);
             },
             success : updateOrganization()
         }).done(function(){
                 $('#removeAssociationModal').modal('hide');
                 updateOrganizationOptions();
             }
     );
 }

function deleteOrganization(organizationId){
     $("#toDelete").text(organizationId);
     $("#impactedElements").empty();
     $('#deleteModal-button').attr('onclick', 'postDeleteOrganization();');
     $('#deleteModal').modal('show');
 }

 function postDeleteOrganization(){
     var organization = $('input[name=organizationId]:checked', '#targets').val();

 	$.ajax({
         type: "DELETE",
         url: "/organization/"+ organization,
         data: {},
         dataType: "html",
         error: function(xhr, error){
             alert("The action cannot be performed: status " + xhr.status);
         }
     }).done(function(){
            cleanAction();
            updateOrganizationOptions();
        }
    );
 }

function createProduct(){
    $('#productEdition').find('#inputProductName').val("");
    $("#productEdition").modal('show');
}

function getProductOverview(){
    var target = $('input:checked', '#targets').val();
    if(target == null){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        updateProductOptions();
        return;
    }

    var productName = $("#productName").val();
    if(target != productName){
        getProductDeliveryOverview(target,productName);
        return;
    }

    var html = "<h3>Project Overview</h3><br/>\n";
    html += "<p><strong>Name:</strong> "+productName+"</p>\n";


    html += "<table class=\"table table-bordered table-hover\" id=\"table-of-result\">\n";
    html += "<thead><tr><th>Module Names</th></tr></thead>\n";
    html += "<tbody>\n";

    $.ajax({
        type: "GET",
        url: "/product/"+ productName + "/modules",
        data: {},
        dataType: "json",
        success: function(data, textStatus) {
            $.each(data, function(i, moduleName) {
                html += "<tr id=\""+moduleName+"-row\"><td name=\"moduleRow\" id=\""+moduleName+"\" onclick=\"removeProductModuleAction('"+moduleName+"');\">" + moduleName + "</td></tr>\n";
            });
            html += "</tbody>\n";
            html += "</table>\n";
            html += "<div id=\"moduleAddDiv\"/>\n";

            html += "<table class=\"table table-bordered table-hover\" id=\"table-of-result\">\n";
            html += "<thead><tr><th>Deliveries</th></tr></thead>\n";
            html += "<tbody>\n";
            $.ajax({
                type: "GET",
                url: "/product/"+ productName + "/deliveries",
                data: {},
                dataType: "json",
                success: function(data, textStatus) {
                    $.each(data, function(i, delivery) {
                        html += "<tr id=\""+delivery+"-row\"><td>" + delivery + "</td></tr>\n";
                    });

                    html += "</tbody>\n<br/>\n";
                    html += "</table>\n";
                    html +="<button type=\"button\" class=\"btn\" style=\"margin:2px;\" onclick=\"createDelivery();\">New Delivery</button>\n";
                    $("#results").empty().append(html);
                    addProjectModuleAction(productName);
                }
            })
        }
    })
}

function createDelivery(){
    $('#deliveryEdition').find('#inputDeliveryName').val("");
    $("#deliveryEdition").modal('show');
}

function deliverySave(){
    var productName = $("#productName").val();
    $.ajax({
        url: "/product/" + productName + "/deliveries",
        method: 'POST',
        contentType: 'application/json',
        data: $('#inputDeliveryName').val(),
        error: function(xhr, error){
            alert("The action cannot be performed: status " + xhr.status);
        }
    }).done(function(){
        cleanAction();
        updateProduct();
    });
}

function addProjectModuleAction(productName){
    var html ="<div class=\"row-fluid\">\n";
    html +="<select id=\"modules\"/>\n";
    html +="<button type=\"button\" class=\"btn\" style=\"margin:2px;\" onclick=\"addProductModule('"+productName+"');\">Add Module</button>\n";
    html +="</div>\n";
    $("#moduleAddDiv").empty().append(html);
    return $.ajax({
        type: "GET",
        url: "/module/names",
        data: {},
        dataType: "json",
        success: function(data, textStatus) {
            $.each(data, function(i, moduleNames) {
                if($('#table-of-result tr > td:contains("'+moduleNames+'")').length == 0){
                    html = "<option value=\"";
                    html += moduleNames + "\">";
                    html += moduleNames + "</option>\n";
                    $("#modules").append(html);
                }
            });
        }
    })
}

function addProductModule(productName){
    var toSend = "[\"";
    toSend += $("#modules").val();
    toSend += "\"";
    $('td[name=moduleRow]').each(function(i, moduleRow) {
        toSend += ",\"";
        toSend += moduleRow.id;
        toSend += "\"";
    });
    toSend += "]";
    $.ajax({
        type: "POST",
        url: "/product/" + productName + "/modules" ,
        data: toSend,
        contentType: 'application/json',
        error: function(xhr, error){
            alert("The action cannot be performed: status " + xhr.status);
        }
    }).done(updateProduct());
}

function removeProductModuleAction(moduleName){
    var productName = $("#productName").val();
    var html ="<div class=\"row-fluid\">The module name "+moduleName+" has been associated to the product <strong>"+productName+"</strong></div>\n";
    html += "\n<div class=\"row-fluid\">Would you like to remove this association?</div>\n";
    $("#removeAssociationModal-text").empty().append(html);

    $('#removeAssociationModal-button').attr('onclick', 'removeProductModule(\''+moduleName+'\');');

    $("#removeAssociationModal").modal('show');
}

function removeProductModule(moduleName){
    var toSend = "[";
    $('td[name=moduleRow]').each(function(i, moduleRow) {
        if(moduleName != moduleRow.id){
            if(toSend.length > 1){
                toSend += ",";
            }
            toSend += "\"";
            toSend += moduleRow.id;
            toSend += "\"";
        }
    });
    toSend += "]";

    var productName = $("#productName").val();
    $.ajax({
        type: "POST",
        url: "/product/" + productName + "/modules" ,
        data: toSend,
        contentType: 'application/json',
        error: function(xhr, error){
            $("#removeAssociationModal").modal('hide');
            alert("The action cannot be performed: status " + xhr.status);
        }
    }).done(function(){
        $("#removeAssociationModal").modal('hide');
        updateProduct();
    });
}

function getProductDeliveryOverview(delivery, product){
    var target = $('input:checked', '#targets').val();
    if(target == null){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        updateProductOptions();
        return;
    }

    var html = "<h3>Delivery Overview</h3><br/>\n";
    html += "<p><strong>Product Name:</strong> "+product+"</p>\n";
    html += "<p><strong>Delivery Name:</strong> "+delivery+"</p>\n";

    html += "<table class=\"table table-bordered table-hover\" id=\"table-of-result\">\n";
    html += "<thead><tr><th>Module Names</th></tr></thead>\n";
    html += "<tbody>\n";

    $.ajax({
        type: "GET",
        url: "/product/"+ product + "/deliveries/" + delivery,
        data: {},
        dataType: "json",
        success: function(data, textStatus) {
            $.each(data, function(i, moduleId) {
                html += "<tr id=\""+moduleId+"-row\"><td name=\"moduleRow\" id=\""+moduleId+"\" onclick=\"removeDeliveryModuleAction('"+moduleId+"');\">" + moduleId + "</td></tr>\n";
            });

            html += "</tbody>\n";
            html += "</table>\n";
            html += "<div id=\"moduleAddDiv\"/>\n";
            $("#results").empty().append(html);
            addDeliveryModuleAction(delivery, product);
        }
    })
}

function addDeliveryModuleAction(delivery, product){
    var html ="<div class=\"row-fluid\">\n";
    html +="<select id=\"modules\"/>\n";
    html +="<button type=\"button\" class=\"btn\" style=\"margin:2px;\" onclick=\"addDeliveryModule('"+delivery+"','"+product+"');\">Add Module</button>\n";
    html +="</div>\n";
    $("#moduleAddDiv").empty().append(html);

    return $.ajax({
        type: "GET",
        url: "/product/" + product +"/modules",
        data: {},
        dataType: "json",
        success: function(data, textStatus) {
            $.each(data, function(i, moduleNames) {
                $.ajax({
                    type: "GET",
                    url: "/module/all?name=" + moduleNames,
                    data: {},
                    dataType: "json",
                    success: function(data, textStatus) {
                        $.each(data, function(i, module) {
                            var moduleId = module.name + ":" + module.version;
                            if($('#table-of-result tr > td:contains("'+moduleId+'")').length == 0){
                                var opt = "<option value=\"";
                                opt += moduleId + "\">";
                                opt += moduleId + "</option>\n";
                                $("#modules").append(opt);
                            }
                        });
                    }
                })
            });
        }
    })
}

function addDeliveryModule(delivery, product){
    var toSend = "[\"";
    toSend += $("#modules").val();
    toSend += "\"";
    $('td[name=moduleRow]').each(function(i, moduleRow) {
        toSend += ",\"";
        toSend += moduleRow.id;
        toSend += "\"";
    });
    toSend += "]";
    $.ajax({
        type: "POST",
        url: "/product/" + product + "/deliveries/" + delivery ,
        data: toSend,
        contentType: 'application/json',
        error: function(xhr, error){
            alert("The action cannot be performed: status " + xhr.status);
        }
    }).done(updateProduct());
}

function removeDeliveryModuleAction(moduleName){
    var target = $('input:checked', '#targets').val();
    var html ="<div class=\"row-fluid\">The module "+moduleName+" has been associated to the delivery <strong>"+target+"</strong></div>\n";
    html += "\n<div class=\"row-fluid\">Would you like to remove this association?</div>\n";
    $("#removeAssociationModal-text").empty().append(html);

    $('#removeAssociationModal-button').attr('onclick', 'removeDeliveryModule(\''+moduleName+'\');');

    $("#removeAssociationModal").modal('show');
}

function removeDeliveryModule(moduleName){
    var toSend = "[";
    $('td[name=moduleRow]').each(function(i, moduleRow) {
        if(moduleName != moduleRow.id){
            if(toSend.length > 1){
                toSend += ",";
            }
            toSend += "\"";
            toSend += moduleRow.id;
            toSend += "\"";
        }
    });
    toSend += "]";

    var productName = $("#productName").val();
    var delivery = $('input:checked', '#targets').val();
    $.ajax({
        type: "POST",
        url: "/product/" + productName + "/deliveries/" + delivery ,
        data: toSend,
        contentType: 'application/json',
        error: function(xhr, error){
            $("#removeAssociationModal").modal('hide');
            alert("The action cannot be performed: status " + xhr.status);
        }
    }).done(function(){
        $("#removeAssociationModal").modal('hide');
        updateProduct();
    });
}

function deleteProduct(){
    var toDelete = $('input:checked', '#targets').val();
    if(toDelete == null){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        updateProductOptions();
        return;
    }

    $("#toDelete").text(toDelete);
    $("#impactedElements").empty();
    $('#deleteModal-button').attr('onclick', 'postDeleteProduct();');
    $('#deleteModal').modal('show');
}

function postDeleteProduct(){
    var productName = $("#productName").val();
    var toDelete = $("#toDelete").text();

    if(productName == toDelete){
        $.ajax({
            type: "DELETE",
            url: "/product/" + productName,
            data: {},
            dataType: "html",
            error: function(xhr, error){
                alert("The action cannot be performed: status " + xhr.status);
            }
        }).done(function(){
                cleanAction();
                updateProductOptions();
            }
        );
        return;
    }

    if(productName != toDelete){
        $.ajax({
            type: "DELETE",
            url: "/product/" + productName + "/deliveries/" + toDelete,
            data: {},
            dataType: "html",
            error: function(xhr, error){
                alert("The action cannot be performed: status " + xhr.status);
            }
        }).done(function(){
                cleanAction();
                updateProductOptions();
            }
        );
        return;
    }
}

function productSave(){
    $.ajax({
        url: "/product",
        method: 'POST',
        contentType: 'application/json',
        data: $('#inputProductName').val(),
        error: function(xhr, error){
            alert("The action cannot be performed: status " + xhr.status);
        }
    }).done(function(){
        cleanAction();
        updateProductOptions();
    });
}


function getModuleOverview(){
    $("#optional-action").empty();

    if($('input[name=moduleId]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }
    var moduleId = $('input[name=moduleId]:checked', '#targets').val();
    var splitter = moduleId.lastIndexOf(':');
	var moduleVersion = moduleId.substring(splitter + 1);
	var moduleName = moduleId.replace(':'+moduleVersion, '');

	$.ajax({
            type: "GET",
            url: "/module/"+ moduleName + "/" + moduleVersion ,
            data: {},
            dataType: "html",
            success: function(data, textStatus) {
                $("#results").empty().append($(data).filter("#module_info"));
                $('.sortable').tablesorter();
            }
        })
}

function getModuleDependencies(){
    $("#optional-action").empty();

    if($('input[name=moduleId]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }

    $("#results").empty().append('<img src="/assets/img/spinner.gif" alt="" id="loader-indicator" />');
    var moduleId = $('input[name=moduleId]:checked', '#targets').val();
    var splitter = moduleId.lastIndexOf(':');
	var moduleVersion = moduleId.substring(splitter + 1);
	var moduleName = moduleId.replace(':'+moduleVersion, '');

	$.ajax({
            type: "GET",
            url: "/module/"+ moduleName + "/" + moduleVersion + "/dependencies?scopeTest=true&scopeRuntime=true&showSources=false" ,
            data: {},
            dataType: "html",
            success: function(data, textStatus) {
                $("#results").empty().append($(data).find("#table_div"));
                $('.sortable').tablesorter();
            }
        })
}

function getModuleThirdParty(){
    $("#optional-action").empty();

    if($('input[name=moduleId]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }

    $("#results").empty().append('<img src="/assets/img/spinner.gif" alt="" id="loader-indicator" />');
    var moduleId = $('input[name=moduleId]:checked', '#targets').val();
    var splitter = moduleId.lastIndexOf(':');
	var moduleVersion = moduleId.substring(splitter + 1);
	var moduleName = moduleId.replace(':'+moduleVersion, '');

	$.ajax({
            type: "GET",
            url: "/module/"+ moduleName + "/" + moduleVersion + "/dependencies?scopeTest=true&scopeRuntime=true&showThirdparty=true&showCorporate=false&showSources=false&showLicenses=true" ,
            data: {},
            dataType: "html",
            success: function(data, textStatus) {
                $("#results").empty().append($(data).find("#table_div"));
                $('.sortable').tablesorter();
            }
        })
}

function getModuleAncestors(){
    $("#optional-action").empty();

    if($('input[name=moduleId]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }

    $("#results").empty().append('<img src="/assets/img/spinner.gif" alt="" id="loader-indicator" />');
    var moduleId = $('input[name=moduleId]:checked', '#targets').val();
    var splitter = moduleId.lastIndexOf(':');
	var moduleVersion = moduleId.substring(splitter + 1);
	var moduleName = moduleId.replace(':'+moduleVersion, '');

	$.ajax({
            type: "GET",
            url: "/module/"+ moduleName + "/" + moduleVersion + "/ancestors" ,
            data: {},
            dataType: "html",
            success: function(data, textStatus) {
                $("#results").empty().append($(data).find("#table_div"));
                $('.sortable').tablesorter();
            }
        })
}

function getModuleLicenses(){
    if($('input[name=moduleId]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }
    $("#results").empty().append('<img src="/assets/img/spinner.gif" alt="" id="loader-indicator" />');
    var moduleId = $('input[name=moduleId]:checked', '#targets').val();
    var splitter = moduleId.lastIndexOf(':');
	var moduleVersion = moduleId.substring(splitter + 1);
	var moduleName = moduleId.replace(':'+moduleVersion, '');
	var queryParams = "showScopes=false&showLicenseUrls=true&showLicenseFullNames=true&showThirdparty=true&showCorporate=false";

	if($('#fullRecursive').hasClass('active')){
	    queryParams += "&showSources=true&showLicenses=false&fullRecursive=true";
	}
	else{
	     queryParams += "&showSources=false&showLicenses=true";
	}

	$.ajax({
            type: "GET",
            url: "/module/"+ moduleName + "/" + moduleVersion + "/dependencies?" + queryParams ,
            data: {},
            dataType: "html",
            success: function(data, textStatus) {
                $("#results").empty().append($(data).find("#table_div"));
                $('.sortable').tablesorter();
            }
        })
}

function getModulePromotionReport(){
    $("#optional-action").empty();

    if($('input[name=moduleId]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }

    $("#results").empty().append('<img src="/assets/img/spinner.gif" alt="" id="loader-indicator" />');
    var moduleId = $('input[name=moduleId]:checked', '#targets').val();
    var splitter = moduleId.lastIndexOf(':');
	var moduleVersion = moduleId.substring(splitter + 1);
	var moduleName = moduleId.replace(':'+moduleVersion, '');

	$.ajax({
            type: "GET",
            url: "/module/"+ moduleName + "/" + moduleVersion + "/promotion/report?fullRecursive=true" ,
            data: {},
            dataType: "html",
            success: function(data, textStatus) {
                $("#results").empty().append($(data).find("#list"));
            }
        })
}

function getArtifactOverview(){
    if($('input[name=gavc]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }
	var gavc = $('input[name=gavc]:checked', '#targets').val();

	$.ajax({
            type: "GET",
            url: "/artifact/"+ gavc,
            data: {},
            dataType: "html",
            success: function(data, textStatus) {
                console.log($(data).filter("#artifact_info"))
                $("#results").empty().append($(data).filter("#artifact_info"));
            }
        }).done(updateArtifactAction());
}

function changeArtifactAction(){
	var gavc = $('input[name=gavc]:checked', '#targets').val();

	$.ajax({
            type: "GET",
            url: "/artifact/"+ gavc,
            data: {},
            dataType: "json",
            success: function(data, textStatus) {
                $('#artifactEdition').find('#inputDownloadUrl').val(data.downloadUrl);
                $('#artifactEdition').find('#inputProvider').val(data.provider);
            }
        });

    var html ="<div class=\"row-fluid\">\n";
    html +="<button type=\"button\" class=\"btn\" style=\"margin:2px;\" onclick=\"$('#artifactEdition').modal('show');\">Update</button>\n";
    html +="</div>\n";
    $("#extra-action").empty().append(html);
}


function updateArtifact(){
	var gavc = $('input[name=gavc]:checked', '#targets').val();
    var downloadUrl = $('#artifactEdition').find('#inputDownloadUrl').val();
	$.ajax({
            type: "POST",
            url: "/artifact/"+ gavc + "/downloadurl?url="+ downloadUrl,
            data: {},
            dataType: "html",
            error: function(xhr, error){
                alert("The action cannot be performed: status " + xhr.status);
            }
    });


    var provider = $('#artifactEdition').find('#inputProvider').val();
	$.ajax({
            type: "POST",
            url: "/artifact/"+ gavc + "/provider?provider="+ provider,
            data: {},
            dataType: "html",
            error: function(xhr, error){
                alert("The action cannot be performed: status " + xhr.status);
            }
    });

    cleanAction();
}

function getArtifactAncestors(){
    if($('input[name=gavc]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }
	var gavc = $('input[name=gavc]:checked', '#targets').val();

	$.ajax({
            type: "GET",
            url: "/artifact/"+ gavc + "/ancestors",
            data: {},
            dataType: "html",
            success: function(data, textStatus) {
                $("#results").empty().append($(data).find("#table_div"));
                $('.sortable').tablesorter();
            }
        })
}

function doNotUseArtifact(){
    if($('input[name=gavc]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }

    var gavc = $('input[name=gavc]:checked', '#targets').val();

	$.ajax({
            type: "GET",
            url: "/artifact/"+ gavc + "/donotuse",
            data: {},
            dataType: "html",
            success: function(donotUse, textStatus) {
                if(donotUse == "true"){
                    $("#doNotUseArtifactModal-text").empty().append(gavc + " is currently flagged with \"DO_NOT_USE\", do you want to un-flagged it?")
                }
                else{
                    $("#doNotUseArtifactModal-text").empty().append("Do you want to flag " + gavc + " with \"DO_NOT_USE\"")
                }
                $('#doNotUseArtifactModal').modal('show');
            }
    })

}

function postDoNotUse(){
    var gavc = $('input[name=gavc]:checked', '#targets').val();
    var doNotUse = $("#doNotUseArtifactModal-text").text().indexOf("you want to flag") >= 0;
    $.ajax({
            type: "POST",
            url: "/artifact/"+ gavc + "/donotuse?doNotUse=" + doNotUse,
            data: {},
            dataType: "html",
            error: function(xhr, error){
                alert("The action cannot be performed: status " + xhr.status);
            }
    }).done($('#doNotUseArtifactModal').modal('hide'));

    cleanAction()
}

function getArtifactLicenses(){
    if($('input[name=gavc]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }
	var gavc = $('input[name=gavc]:checked', '#targets').val();

	var html = "<table class=\"table table-bordered table-hover\" id=\"table-of-result\">\n";
	html += "<thead><tr><td>Licenses</td></tr></thead>\n";
	html += "<tbody>\n";
    
	$.ajax({
            type: "GET",
            url: "/artifact/"+ gavc + "/licenses",
            data: {},
            dataType: "json",
            success: function(data, textStatus) {
                $.each(data, function(i, license) {
                    if(license.unknown){
                        html += "<tr id=\""+license.name+"-row\"><td onclick=\"removeLicenseAction('"+license.name+"');\"><strong>" + license.name + "</strong> (to be identified)</td></tr>\n";
                    }
                    else{
                        html += "<tr id=\""+license.name+"-row\"><td onclick=\"removeLicenseAction('"+license.name+"');\">" + license.name + "</td></tr>\n";
                    }
                });

	            html += "</tbody>\n";
                $("#results").empty().append(html);
            }
    }).done(updateLicenseAction());
}

function addLicenseAction(){
    var html ="<div class=\"row-fluid\">\n";
    html +="<select id=\"licenses\">\n";
    return $.ajax({
            type: "GET",
            url: "/license/names",
            data: {},
            dataType: "json",
            success: function(data, textStatus) {
                $.each(data, function(i, licenseName) {
                    if($('#table-of-result tr > td:contains("'+licenseName+'")').length == 0){
                        html += "<option value=\"";
                        html += licenseName + "\">";
                        html += licenseName + "</option>\n";
                    }
                });

                html +="</select>\n";
                html +="<button type=\"button\" class=\"btn\" style=\"margin:2px;\" onclick=\"addLicense();\">Add</button>\n";
                html +="</div>\n";
                $("#extra-action").empty().append(html);
            }
    })
}

function removeLicenseAction(licenseId){
    $('#removeAssociationModal-button').attr('onclick', 'removeLicense(\''+licenseId+'\');');

    var gavc = $('input[name=gavc]:checked', '#targets').val();

	$.ajax({
        type: "GET",
        url: "/license/"+ licenseId,
        data: {},
        dataType: "html",
        success: function(data, textStatus) {
            var html ="<div class=\"row-fluid\">The following license has been associated with <strong>"+gavc+"</strong></div>\n";
            html += $(data).filter(".row-fluid").html();
            html += "\n<div class=\"row-fluid\">Would you like to remove this association?</div>\n";
            $("#removeAssociationModal-text").empty().append(html);
        },
        error:function (xhr, ajaxOptions, thrownError){
            if(xhr.status==404) {
            var html ="<div class=\"row-fluid\">An unknown license as been associated to <strong>"+gavc+"</strong></div>\n";
            html += "\n<div class=\"row-fluid\"><br/>Two cases are possible:<br/>"+
                    " - identify the license among the existing ones then remove this association<br/>"+
                    " - create a new license and then add it to this artifact, then remove this association<br/><br/></div>\n";
            html += "\n<div class=\"row-fluid\">Would you like to remove this association now?</div>\n";
            $("#removeAssociationModal-text").empty().append(html);
            }
        }
    }).done($('#removeAssociationModal').modal('show'));
}

function addLicense(){
    var licenseId = $("#licenses").val();
    var gavc = $('input[name=gavc]:checked', '#targets').val();

    $.ajax({
            type: "POST",
            url: "/artifact/" + gavc + "/licenses?licenseId=" + licenseId ,
            data: {},
            dataType: "html",
            error: function(xhr, error){
                alert("The action cannot be performed: status " + xhr.status);
            }
        }).done(updateLicenses());
}

function removeLicense(licenseId){
    var gavc = $('input[name=gavc]:checked', '#targets').val();

    $.ajax({
            type: "DELETE",
            url: "/artifact/" + gavc + "/licenses?licenseId=" + licenseId ,
            data: {},
            dataType: "html",
            success: function(data, textStatus) {
                getArtifactLicenses();
            },
            error: function(xhr, error){
                alert("The action cannot be performed: status " + xhr.status);
            }
    }).done($('#removeAssociationModal').modal('hide'));
}

function createLicense(){
    $('#licenseEdition').find('#inputName').val("");
    $('#licenseEdition').find('#inputLongName').val("");
    $('#licenseEdition').find('#inputURL').val("");
    $('#licenseEdition').find('#inputComments').val("");
    $('#licenseEdition').find('#inputRegexp').val("");
	$("#licenseEdition").modal('show');
}

function licenseSave(){
    $.ajax({
        url: "/license",
        method: 'POST',
        contentType: 'application/json',
        data: '{ "name": "'+$('#inputName').val()+'", "longName": "'+$('#inputLongName').val()+'", "comments": "'+$('#inputComments').val()+'", "regexp": "'+$('#inputRegexp').val()+'", "url": "'+$('#inputURL').val()+'", "approved": false }',
        error: function(xhr, error){
            alert("The action cannot be performed: status " + xhr.status);
        }
    }).done(function(){
            cleanAction();
            updateLicenseOptions();
    });
}

function getLicenseOverview(){
    if($('input[name=licenseId]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }
	var licenseId = $('input[name=licenseId]:checked', '#targets').val();

	$.ajax({
            type: "GET",
            url: "/license/"+ licenseId,
            data: {},
            dataType: "html",
            success: function(data, textStatus) {
                $("#results").empty().append($(data).filter("#license_info"));
            }
        })

    $("#extra-action").empty().append("<button type=\"button\" class=\"btn btn-danger\" style=\"margin:2px;\" onclick=\"deleteLicense('"+licenseId+"');\">Delete</button>\n");
    $("#extra-action").append("<button type=\"button\" class=\"btn\" style=\"margin:2px;\" onclick=\"editLicense('"+licenseId+"');\">Edit</button>\n");
}

function deleteLicense(licenseId){
    $("#toDelete").text(licenseId);
    $("#impactedElements").empty();
    $('#deleteModal-button').attr('onclick', 'postDeleteLicense();');


    $.ajax({
        type: "GET",
        accept: {
            json: 'application/json'
        },
        url: "/artifact/all?licenseId=" + licenseId,
        success: function(data, textStatus) {
           var impactedElements = "";
            $.each(data, function(i, artifact) {
                impactedElements += "<li class=\"active\" style=\"cursor: pointer\">";
                impactedElements += artifact.groupId + ":" +artifact.artifactId + ":" +artifact.version + ":" +artifact.classifier + ":" + artifact.extension;
                impactedElements += "</li>";

            });

            if(impactedElements.length){
                $("#impactedElements").append("<strong>The license is used by the following artifact(s):<strong><br/>");
                $("#impactedElements").append("<ul>" + impactedElements +"</ul>")
            }
            else{
                 $("#impactedElements").append("No artifact is using this license.");
            }

        },
        error: function(xhr, error){
            alert("The action cannot be performed: status " + xhr.status);
        }
    }).done($('#deleteModal').modal('show'));
}



function postDeleteLicense(){
	var licenseId = $('input[name=licenseId]:checked', '#targets').val();

	$.ajax({
        type: "DELETE",
        url: "/license/"+ licenseId,
        data: {},
        dataType: "html",
        error: function(xhr, error){
            alert("The action cannot be performed: status " + xhr.status);
        }
    }).done(function(){
                        cleanAction();
                        updateLicenseOptions();
                }
    );
}

function editLicense(licenseId){
    $.ajax({
        type: "GET",
        url: "/license/"+ licenseId,
        data: {},
        dataType: "json",
        success: function(license, textStatus) {
            $('#licenseEdition').find('#inputName').val(license.name);
            $('#licenseEdition').find('#inputLongName').val(license.longName);
            $('#licenseEdition').find('#inputURL').val(license.url);
            $('#licenseEdition').find('#inputComments').val(license.comments);
            $('#licenseEdition').find('#inputRegexp').val(license.regexp);
        }
    }).done($("#licenseEdition").modal('show'));
}

function approveLicense(){
    if($('input[name=licenseId]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }
	var licenseId = $('input[name=licenseId]:checked', '#targets').val();

    $.ajax({
        type: "POST",
        url: "/license/"+ licenseId +"?approved=true",
        data: {},
        error: function(xhr, error){
            alert("The action cannot be performed: status " + xhr.status);
        },
        success: function(data, textStatus) {
            $("#messageAlert").empty().append("<strong>Operation performed.</strong>");
            $("#anyAlert").show();
            cleanAction();
        }
    });
}

function rejectLicense(){
    if($('input[name=licenseId]:checked', '#targets').size() == 0){
        $("#messageAlert").empty().append("<strong>Warning!</strong> You must select a target before performing an action.");
        $("#anyAlert").show();
        return;
    }
	var licenseId = $('input[name=licenseId]:checked', '#targets').val();

    $.ajax({
        type: "POST",
        url: "/license/"+ licenseId +"?approved=false",
        data: {},
        error: function(xhr, error){
            alert("The action cannot be performed: status " + xhr.status);
        },
        success: function(data, textStatus) {
            $("#messageAlert").empty().append("<strong>Operation performed.</strong>");
            $("#anyAlert").show();
            cleanAction();
        }
    });
}

/*********************/
/*      Utils       */
/*********************/
function cleanAction(){
    $(".action-button").removeClass('active');
    $("#results").empty();
    $("#optional-action").empty();
    $("#extra-action").empty();
}

/*WorkAround*/
function updateOrganization(){
    setTimeout(function(){
      getOrganizationOverview();
    }, 500);
}

/*WorkAround*/
function updateLicenses(){
    setTimeout(function(){
      getArtifactLicenses();
    }, 500);
}

/*WorkAround*/
function updateArtifactAction(){
    setTimeout(function(){
      changeArtifactAction();
    }, 500);
}

/*WorkAround*/
function updateLicenseAction(){
    setTimeout(function(){
      addLicenseAction();
    }, 500);
}

/*WorkAround*/
function updateLicenseOptions(){
    setTimeout(function(){
      displayLicenseOptions();
    }, 500);
}

/*WorkAround*/
function updateLicenseReport(){
    setTimeout(function(){
      getModuleLicenses();
    }, 500);
}

/*WorkAround*/
function updateOrganizationOptions(){
    setTimeout(function(){
        displayOrganizationOptions();
    }, 500);
}

/*WorkAround*/
function updateProductOptions(){
    setTimeout(function(){
        displayProductOptions();
    }, 500);
}

/*WorkAround*/
function updateProduct(){
    setTimeout(function(){
        getProductOverview();
    }, 500);
}