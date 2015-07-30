var GrapesProduct = {
    initPage: function () {
        var selectedproduct = "";
        //set list of product names
        GrapesCommons.getRestResources(GrapesProductUrls.listNames, GrapesProductViews.setProductList);
//todo should check the size of the list if empty write error msg and stop
        $('#productList').click(function () {
            selectedproduct = $("#productList option:selected").text();
            $(document.body).data("productName", selectedproduct);


            //retrieve the version numbers fro the product list.
            //todo should either display the first version in the list automaticall or clear the page and put a message to
            //todo to choose the version
            GrapesCommons.getRestResources(GrapesProductUrls.product(selectedproduct), GrapesProductViews.createproductViews);


        });


        $('#createProductSaveBtn').click(GrapesProductHandlers.createProduct);
        $('#delProceedBtn').click(GrapesProductHandlers.deleteProduct);
        $('#addModule').click(GrapesProductHandlers.addModuleToProduct);
        $('#addDelivery').click(GrapesProductHandlers.addDeliveryToProduct);



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

var GrapesProductUrls = {

    root: "/product",
    listNames: "/product/names",
    listModuleNames: "/module/names",
    productDependencies: function (productName, productVersion) {
        return this.root + "/" + encodeURIComponent(productName) + "/" + encodeURIComponent(productVersion) + "/dependencies";
    },

    productModules: function (productName) {
        return this.root + "/" + encodeURIComponent(productName) + "/modules";
    },
    productDeliveries: function (productName) {

        return this.root + "/" + encodeURIComponent(productName) + "/deliveries";
    },

    product: function (productName) {
        return this.root + "/" + encodeURIComponent(productName);
    }
}

/****************************************Button Handlers************************************************/
var GrapesProductHandlers = {
    createProduct: function () {

        var productName = $('#createProductName').find('input[name="productName"]').val();

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

            $('#createProductName').find('input[name="productName"]').val('');
            $("#createProductModal").modal('hide');
        }
        return;
    },
    addDeliveryToProduct: function () {

        var deliveryName = $('#productDeliverySection').find('input[id="addDeliveryName"]').val();
        GrapesCommons.postRestResource(GrapesProductUrls.productDeliveries($(document.body).data("productName")), '"' + deliveryName + '"', GrapesProduct.reloadPage);
        $('#addDeliveryName').attr('placeholder', "Enter new delevery name").val('');
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
        $('#deleteProductModal').modal('hide');
    },
    removeDeliveryOrModule: function (target, removalItem) {
        //todo

    }


}
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
        $('#productList').empty().append(option);

        return;
    },
    showAdminElements: function () {
        GrapesCommons.setIsAdmin();
        if (GrapesCommons.getIsAdmin()) {

            $("#deleteProductBtn").show();
            $("#addDelivery").show();
            $("#addDeliveryName").show();
            $('#moduleList').show();
            $("#addModule").show();
        }
    },
    createModuleChoiceList: function (jsonData) {

        var option = '';
        for (var i = 0; i < jsonData.length; i++) {
            option += '<option value="' + jsonData[i] + '">' + jsonData[i] + '</option>';
        }
        $('#moduleList').empty().append(option);
        // $("#addModule").show();

        return;

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
/*******************************product Tabs************************************/
var GrapesProductTabOverview = {
    createTab: function (json) {
        var tabletitle = "Product Information";
        var productId = document.getElementById("productId");
        var table = $("<table/>").addClass(' table table-striped');
        $("#productId").text(json._id);
        $(document.body).data("productModules", json.modules);

        table.append("<thead><tr><td>" + tabletitle + "</td> </tr></thead>");
        table.append("<tr><td>Module Names</td><td></td></tr>");

        $.each(json.modules, function (key, val) {
            if (key !== "_id") {
                var col1 = $("<td>").text(key);
                var col2 = $("<td>").text(val);
                var row = $("<tr/>").append(col1).append(col2);

                table.append(row);
            }
            console.log("key", key, " value ", val);

        });

        GrapesCommons.getRestResources(GrapesProductUrls.listModuleNames, GrapesProductViews.createModuleChoiceList);
        //todo need to make these clickableable and each time you click it builds the info on the second part of the page

        table.append("<tr><td>Delivery Information</td> </tr>");

        $.each(json.deliveries, function (key, val) {
            if (key !== "_id") {
                var col1 = $("<td>").text(key);
                var col2 = $("<td>").text(val);
                var row = $("<tr/>").append(col1).append(col2);

                table.append(row);
            }
        });
        $("#productModuleTable").empty().append(table);
        $("#productInfo").hide();


        GrapesProductViews.showAdminElements();


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


/****************************************INIT Functions**************************************************/



$(document).ready(function () {
    //maybe we call this with a body onload for the page?
    GrapesProduct.initPage();
});