/**
 * Basic functions for Grapes Product page.
 * @type {{initPage: Function, reloadPage: Function}}
 */
var GrapesProduct = {
    initPage: function () {
        var selectedproduct = "";
        //set list of product names
        GrapesCommons.getRestResources(GrapesProductUrls.listNames, GrapesProductViews.setProductList);
//todo should check the size of the list if empty write error msg and stop
        $(GrapesProductHtmlRefs.productList).click(function () {
            selectedproduct = $("#productList option:selected").text();
            $(document.body).data("productName", selectedproduct);


            //retrieve the version numbers fro the product list.
            //todo should either display the first version in the list automaticall or clear the page and put a message to
            //todo to choose the version
            GrapesCommons.getRestResources(GrapesProductUrls.product(selectedproduct), GrapesProductViews.createproductViews);


        });


        $(GrapesProductHtmlRefs.createProductSaveBtn).click(GrapesProductHandlers.createProduct);
        $(GrapesProductHtmlRefs.delProceedBtn).click(GrapesProductHandlers.deleteProduct);
        $(GrapesProductHtmlRefs.addModuleBtn).click(GrapesProductHandlers.addModuleToProduct);
        $(GrapesProductHtmlRefs.addDeliveryBtn).click(GrapesProductHandlers.addDeliveryToProduct);


    },
    reloadPage: function () {
        //todo need to fix the relaod page
        GrapesCommons.getRestResources(GrapesProductUrls.listNames, GrapesProductViews.setproductList);
        if ($(document.body).data("productName") === undefined) {
            GrapesCommons.load();//todo no longer defined put this in grapes commons
        } else {
            GrapesCommons.getRestResources(GrapesProductUrls.product($(document.body).data("productName")), GrapesProductViews.createproductViews);
        }
    }

}

/**
 * The Grapes REST API end points.
 * @type {{root: string, listNames: string, listModuleNames: string, product: Function, productDependencies: Function, productModules: Function, productDeliveries: Function, delivery: Function}}
 */
var GrapesProductUrls = {

    root: "/product",
    listNames: "/product/names",
    listModuleNames: "/module/names",
    product: function (productName) {
        return this.root + "/" + encodeURIComponent(productName);
    },
    productDependencies: function (productName, productVersion) {
        return this.root + "/" + encodeURIComponent(productName) + "/" + encodeURIComponent(productVersion) + "/dependencies";
    },

    productModules: function (productName) {
        return this.root + "/" + encodeURIComponent(productName) + "/modules";
    },
    productDeliveries: function (productName) {

        return this.root + "/" + encodeURIComponent(productName) + "/deliveries";
    },

    delivery: function(productName,deliveryName){
        return this.root+  "/" + encodeURIComponent(productName) + "/deliveries/"+encodeURIComponent(deliveryName);
    }
}

/**
 * Variables from the html page mostly id names of tags.
 * @type {{addDeliveryNameInput: string, moduleList: string, productList: string, createProductModal: string, deleteProductModal: string, productModuleTable: string, productDeliveryTable: string, createProductSaveBtn: string, delProceedBtn: string, addModuleBtn: string, addDeliveryBtn: string, createProductName: string, productModuleSection: string, productDeliverySection: string, productId: string, productInfo: string, deleteProductBtn: string, devTable: string}}
 */
var GrapesProductHtmlRefs = {
    addDeliveryNameInput: "#addDeliveryNameInput",
    moduleList: "moduleList",
    productList: "#productList",

    createProductModal: "#createProductModal",
    deleteProductModal: "#deleteProductModal",

    productModuleTable: "#productModuleTable",
    productDeliveryTable: "#productDeliveryTable",

    createProductSaveBtn: "#createProductSaveBtn",
    delProceedBtn: "#delProceedBtn",
    addModuleBtn: "#addModuleBtn",
    addDeliveryBtn: "#addDeliveryBtn",

    createProductName: "#createProductName",
    productModuleSection: "#productModuleSection",
    productDeliverySection: "#productDeliverySection",
    productId: "#productId",
    productInfo: "#productInfo",
    deleteProductBtn: "#deleteProductBtn",

    devTable:"#devTable"

}

/**
 * Event Handlers.
 * Function associated with events such as click events.
 * @type {{createProduct: Function, addDeliveryToProduct: Function, addModuleToProduct: Function, deleteProduct: Function, removeDeliveryOrModule: Function, getActionBarForExtensionOrgs: Function}}
 */
var GrapesProductHandlers = {

    createProduct: function () {

        var productName = $(GrapesProductHtmlRefs.createProductName).find('input[name="productName"]').val();

        if (!productName || productName.length === 0) {
            alert("product name required");
        }
        else {
            var data = '"' + productName + '"';


            //should refresh list as the call back.
            $(document.body).data("productName", productName);
            GrapesCommons.postRestResource(GrapesProductUrls.root, data, GrapesProduct.reloadPage);
            //todo should go in its own call back method I put then in post done for now
            // GrapesCommons.getRestResources(GrapesProductUrls.listNames,GrapesProductsetproductList);
            // reloadPage("do", productName);

            $(GrapesProductHtmlRefs.createProductName).find('input[name="productName"]').val('');
            $(GrapesProductHtmlRefs.createProductModal).modal('hide');
        }
        return;
    },
    addDeliveryToProduct: function () {

        var deliveryName = $(GrapesProductHtmlRefs.productModuleSection).find('input[id="addDeliveryNameInput"]').val();
        GrapesCommons.postRestResource(GrapesProductUrls.productDeliveries($(document.body).data("productName")), '"'
        + deliveryName + '"', GrapesProduct.reloadPage);
        $(GrapesProductHtmlRefs.addDeliveryButton).attr('placeholder', "Enter new delevery name").val('');
    },

    addModuleToProduct: function () {

        var moduleToAdd = $("#moduleList option:selected").text();

        var moduleList = [];

        var currentListofModules = $(document.body).data("productModules");
        console.log("current list" + currentListofModules + " length " + currentListofModules.length);

        for (i = 0; i < currentListofModules.length; i++) {
            console.log("at model: " + currentListofModules[i]);
            moduleList.push(currentListofModules[i]);
        }

        if ($.inArray(moduleToAdd, moduleList) < 0) {
            moduleList.push(moduleToAdd);
            console.log("modules list: " + moduleList);
            moduleList = '["' + moduleList.join('","') + '"]';

            GrapesCommons.postRestResource(GrapesProductUrls.productModules($(document.body).data("productName")), moduleList, GrapesProduct.reloadPage);
        }


    },
    deleteProduct: function () {
        //todo add a response notifaction to the html page
        console.log("DELETE ME muahahahah");

        GrapesCommons.deleteRestResource(GrapesProductUrls.product($(document.body).data("productName")), GrapesCommons.load);
        $(GrapesProductHtmlRefs.deleteProductModal).modal('hide');
    },
    removeDeliveryOrModule: function (target, removalItem) {
        //todo

    },
    getActionBarForExtensionOrgs: function (orgName, corpId) {
    var bar = $("<div></div>").addClass("bundle-action-bar pull-right").addClass("btn-toolbar").attr("role",
        "toolbar");
    var inner = $("<div></div>").addClass("btn-group");
    var uninstall = $("<button type=\"button\" class=\"btn btn-default btn-xs\"><span class=\"glyphicon glyphicon-remove\"></span></button>");
    uninstall.click(function () {
        GrapesCommons.deleteRestResource(OrgUrls.getCorpIdUrl(orgName, corpId), reloadOrgPage, orgName);

    });

    inner.append(uninstall);

    bar.append(inner);
    return $("<td></td>").addClass("isAdminhide").append(bar).hide();
}


}

/**
 * Views.
 * Functions associated with the creating the page view.
 * @type {{createproductViews: Function, setProductList: Function, showAdminElements: Function, createModuleChoiceList: Function, getActionBarForExtension: Function}}
 */
var GrapesProductViews = {
    createproductViews: function (jsonData) {
        GrapesProductTabOverview.createTab(jsonData);

    },
    setProductList: function (jsonData) {
        // alert("i");
        var option = '';
        for (var i = 0; i < jsonData.length; i++) {
            option += '<option value="' + jsonData[i] + '">' + jsonData[i] + '</option>';
        }
        $(GrapesProductHtmlRefs.productList).empty().append(option);

        return;
    },
    showAdminElements: function () {
        GrapesCommons.setIsAdmin();
        if (GrapesCommons.getIsAdmin()) {

            $(GrapesProductHtmlRefs.deleteProductBtn).show();
            $(GrapesProductHtmlRefs.addDeliveryBtn).show();
            $(GrapesProductHtmlRefs.addDeliveryNameInput).show();
            $("#moduleList").show();
            $(GrapesProductHtmlRefs.addModuleBtn).show();
        }
    },
    createModuleChoiceList: function (jsonData) {

        var option = '';
        for (var i = 0; i < jsonData.length; i++) {
            option += '<option value="' + jsonData[i] + '">' + jsonData[i] + '</option>';
        }
        $("#moduleList").empty().append(option);
        // $("#addModule").show();



    },
    getActionBarForExtension: function (licName, licFieldKey, licFieldVal) {
        var bar = $("<div></div>").addClass("bundle-action-bar pull-right").addClass("btn-toolbar").attr("role",
            "toolbar");
        var inner = $("<div></div>").addClass("btn-group");

        var extupdate = $("<button type=\"button\" class=\"btn btn-default btn-xs\" data-toggle=\"modal\" data-target=\"#corpIdModal\"><span class=\"glyphicon glyphicon-repeat\"></span></button>");
        extupdate.click(function () {
            GrapesProductHandlers.removeDeliveryOrModule(licFieldKey, licFieldVal, licName)
        });

        inner.append(extupdate);
        bar.append(inner);
        return $("<td></td>").append(bar);
    }

}

/**
 * Product Overview Tab.
 * Functions used in creating the overview tab.
 * @type {{createTab: Function, createDeliveryOverview: Function}}
 */
var GrapesProductTabOverview = {
    createTab: function (json) {
        var tabletitle = "Product Information";
        var productId = document.getElementById("productId");
        var table = $("<table/>").addClass(' table table-striped');
        $(GrapesProductHtmlRefs.productId).text(json._id);
        $(document.body).data("productModules", json.modules);

        table.append("<thead><tr><td>" + tabletitle + "</td> </tr></thead>");
        table.append("<tr><td>Module Names</td><td></td></tr>");

        //create a table listing all modules in a product
        $.each(json.modules, function (key, val) {

            if (key !== "_id") {
                var col1 = $("<td>").text(key);
                var col2 = $("<td>").text(val);
                //var row = $("<tr/>").append(col1).append(col2);
                var row = $("<tr/>").append(col2);
                table.append(row);
            }
            console.log("key", key, " value ", val);

        });

        GrapesCommons.getRestResources(GrapesProductUrls.listModuleNames, GrapesProductViews.createModuleChoiceList);
        //todo need to make these clickableable and each time you click it builds the info on the second part of the page

        //create a table of all deliveries for a modal
        var row = "<tr/>";

        table.append("<tr><td>Delivery Information</td> </tr>");
        $.each(json.deliveries, function (key, val) {
            console.log("key", key, " value ", val);

            if (key !== "_id") {
                var namevalCol = $("<td>").append($('<a>').text(key).click(function () {
                    $(document.body).data("deliveryName",key);
                    GrapesCommons.getRestResources(GrapesProductUrls.delivery($(document.body).data("productName"),key),GrapesProductTabOverview.createDeliveryOverview);
                }));
                var col2 = $("<td>").text(val);//value contains module list for deliveries

                var row = $("<tr/>").append(namevalCol).append(col2);

                table.append(row);
            }
        });
        $(GrapesProductHtmlRefs.productModuleTable).empty().append(table);
        $(GrapesProductHtmlRefs.productInfo).hide();


        GrapesProductViews.showAdminElements();


    },
    createDeliveryOverview: function(jsonData){
        //todo it would be nice if the paragrapgh displayed which delivery it for
        $(GrapesProductHtmlRefs.productDeliverySection).text("Delivery Information for : "+$(document.body).data("deliveryName"));
        console.log(jsonData);
        $(GrapesProductHtmlRefs.productDeliveryTable).empty();
        var table = $('<table/>', {
            class: "table table-striped",
            id: "devTable"
        });
        if(jsonData.length>0){
            console.log("Imade it here length is "+jsonData.length);
            $.each(jsonData, function(key, val){
               // var col1 = $("<td>").text(key);
                var col2 = $("<td>").text(val).addClass(key);
                var row = $("<tr/>").append(col2);
                table.append(row);

            })
            $(GrapesProductHtmlRefs.productDeliveryTable).append(table);

        }
        else{
            $(GrapesProductHtmlRefs.productDeliveryTable).append("<p> No Delivery Data Available</p>");
        }

    }

}
var GrapesProductTabDependcies = {}
var GrapesProductTabLicences = {
    createTab: function () {
        //todo
    }
}

var GrapesProductTabPromtionReport = {
    createTab: function () {
    }
}





$(document).ready(function () {
    //maybe we call this with a body onload for the page?
    GrapesProduct.initPage();
});