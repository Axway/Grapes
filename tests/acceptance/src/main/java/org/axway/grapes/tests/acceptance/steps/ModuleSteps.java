package org.axway.grapes.tests.acceptance.steps;


import net.thucydides.core.pages.Pages;
import org.axway.grapes.commons.datamodel.Dependency;
import org.axway.grapes.commons.datamodel.Module;
import org.axway.grapes.commons.datamodel.Scope;
import org.axway.grapes.tests.acceptance.TestConfiguration;
import org.axway.grapes.tests.acceptance.pages.ListPage;
import org.axway.grapes.tests.acceptance.pages.ModulePage;
import org.axway.grapes.tests.acceptance.pages.PromotionReportPage;
import org.axway.grapes.utils.client.GrapesCommunicationException;

import javax.naming.AuthenticationException;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestCase.assertEquals;
import static net.thucydides.core.matchers.BeanMatchers.the;
import static net.thucydides.core.pages.PageObject.withParameters;
import static org.hamcrest.Matchers.*;

public class ModuleSteps extends GrapesAbstractSteps {

    private final TestConfiguration config = TestConfiguration.getInstance();

    private PromotionReportPage promotionReportPage = getPages().get(PromotionReportPage.class);
    private ModulePage modulePage = getPages().get(ModulePage.class);
    private ListPage listPage = getPages().get(ListPage.class);
	
	public ModuleSteps(final Pages pages) {
		super(pages);
        promotionReportPage.setDefaultBaseUrl(config.getGrapesBaseUrl());
        modulePage.setDefaultBaseUrl(config.getGrapesBaseUrl());
        listPage.setDefaultBaseUrl(config.getGrapesBaseUrl());
	}

    //OPEN NEW PAGES

    public void look_for_module(final String name, final String version) {
        modulePage.open("module.info", withParameters(name, version));
    }

    public void look_for_module_names() {
        listPage.open("module.names", withParameters());
    }

    public void look_for_module_versions(final String moduleName) {
        listPage.open("module.versions", withParameters(moduleName));
    }

    public void look_for_module_dependencies(final String moduleName, final String moduleVersion) {
        listPage.open("module.dependencies", withParameters(moduleName, moduleVersion));
    }

    public void look_for_product_thirdparty(final String moduleName, final String moduleVersion) {
        listPage.open("product.thirdparty", withParameters(moduleName, moduleVersion));
    }

    public void look_for_module_ancestors(final String moduleName, final String moduleVersion) {
        listPage.open("module.ancestors", withParameters(moduleName, moduleVersion));
    }

    public void look_for_product_dependency_report(final String moduleName, final String moduleVersion) {
        listPage.open("product.dependency.report", withParameters(moduleName, moduleVersion));
    }

    public void look_for_product_licenses(final String moduleName, final String moduleVersion) {
        listPage.open("product.licenses", withParameters(moduleName, moduleVersion));
    }

    public void getPromotionReport(final String moduleName, final String moduleVersion) {
        promotionReportPage.open("module.promotion.report.recursive", withParameters(moduleName, moduleVersion));
    }

    // POST OR DELETE
    public void postModule(final Module module) throws GrapesCommunicationException, AuthenticationException {
        final TestConfiguration config = TestConfiguration.getInstance();
        getClient().postModule(module, config.getGrapesNotifier(), config.getGrapesNotifierPwd());
    }

    public void delete_module(final String moduleName, final String moduleVersion) throws GrapesCommunicationException, AuthenticationException {
        final TestConfiguration config = TestConfiguration.getInstance();
        getClient().deleteModule(moduleName, moduleVersion, config.getGrapesNotifier(), config.getGrapesNotifierPwd());
    }

    public void promoteModule(final String moduleName, final String moduleVersion) throws GrapesCommunicationException, AuthenticationException {
        final TestConfiguration config = TestConfiguration.getInstance();
        getClient().promoteModule(moduleName, moduleVersion, config.getGrapesNotifier(), config.getGrapesNotifierPwd());
    }

    // PAGE CHECKS
    public void should_display_module_info(final String name, final String version) {
        modulePage.displays_name(name);
        modulePage.displays_version(version);
    }


    public void should_display_module_artifact(final String artifactGroupid, final String artifactId, final String artifactVersion, final String artifactClassifier, final String artifactType, final String artifactExtension) {
        modulePage.has_artifact(the("GroupId", is(artifactGroupid)),
                the("ArtifactId", is(artifactId)),
                the("Version", is(artifactVersion)),
                the("Classifier", is(artifactClassifier)),
                the("Type", is(artifactType)),
                the("Extension", is(artifactExtension)));
    }

    public void should_display_module_names(final List<String> names) {
        for(String name: names){
            listPage.has_info(the("name", is(name)));
        }
    }

    public void should_display_module_versions(final List<String> versions) {
        for(String version: versions){
            listPage.has_info(the("version", is(version)));
        }
    }

    public void should_display_module_dependencies(final List<Dependency> dependencies) {
        for(Dependency dependency: dependencies){
            listPage.has_info(the("Target", equalTo(dependency.getTarget().getGavc())), the("Scope", equalTo(dependency.getScope().toString().toLowerCase())));
        }
    }

    public void should_display_module_ancestor(final Module ancestor, final String target, final Scope scope) {
        listPage.has_info(the("Source", containsString(ancestor.getName())), the("Target", equalTo(target)), the("Scope", equalTo(scope.toString().toLowerCase())));
    }

    public void should_be_promoted() {
        modulePage.is_promoted();
    }

    public void should_get_error_404() {
        assertEquals("Error 404 Not Found", modulePage.getTitle());
    }

    public void should_see_not_up_to_date(final String groupId, final String artifactId , final String currentVersion, final String lastVersion) {
        listPage.has_info(the("GroupId", equalTo(groupId)),
                the("ArtifactId", equalTo(artifactId)),
                the("Last Release Version", equalTo(lastVersion)),
                the("Current Version", equalTo(currentVersion)));
    }

    public void should_see_license(final String licenseName) {
        listPage.has_info(the("license", containsString(licenseName)));
    }

    public void checkThatTheReportSayingOk() {
        promotionReportPage.promotionIsPossible();
    }

    public void checkPromotionReportFailures(final List<String> shouldNotBeUsed, final List<String> hasToBePromoted) {
        promotionReportPage.promotionIsNotPossible();
        promotionReportPage.shouldNotBeUsed(shouldNotBeUsed);
        promotionReportPage.hasToBePromoted(hasToBePromoted);
    }
}